#include <vector>

#include <MQTT.h>
#include <WiFi.h>
#ifdef __SMCE__
#include <OV767X.h>
#endif

#include <Smartcar.h>

const int forwardSpeed = 50; // % of the full speed forward used in serial steering
const int reverseSpeed   = -70; // % of the full speed backward used in serial steering
const int leftDegrees = -75; // degrees to turn left used in serial steering
const int rightDegrees = 75;  // degrees to turn right used in serial steering
bool movingBackwards = false; //used in serial steering

const auto distanceToObject = 45; //distance to object when the car stops
bool frontDanger = false;
bool rearDanger = false;
int currentThrottle = 0;        //keeps track of current throttle/ "speed"
int currentSteeringAngle = 0;   //keeps track of if we are turning or not


MQTTClient mqtt;
WiFiClient net;

const char ssid[] = "";
const char pass[] = "";

// Copied from Dimitrios' Smartcar shield documentation, initiates the SmartCar
ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);
 
GY50 gyroscope(arduinoRuntime, 37);
 
const auto pulsesPerMeter = 600;
 
DirectionlessOdometer leftOdometer(
    arduinoRuntime, smartcarlib::pins::v2::leftOdometerPin, []() { leftOdometer.update(); },
    pulsesPerMeter);
DirectionlessOdometer rightOdometer(
    arduinoRuntime, smartcarlib::pins::v2::rightOdometerPin, []() { rightOdometer.update();},
    pulsesPerMeter);
 
SmartCar car(arduinoRuntime, control, gyroscope, leftOdometer, rightOdometer);

const auto oneSecond = 20UL;
#ifdef __SMCE__
const auto triggerPin = 6;
const auto echoPin = 7;
const auto mqttBrokerUrl = "127.0.0.1";
#else
const auto triggerPin = 33;
const auto echoPin = 32;
const auto mqttBrokerUrl = "192.168.0.40";
#endif
const auto maxDistance = 200;
SR04 front(arduinoRuntime, triggerPin, echoPin, maxDistance);

std::vector<char> frameBuffer;

void setup() {
  // put your setup code here, to run once:
    Serial.begin(9600); //starts and waits for some time to sensors to start

    //the following part connects to wifi and sends the camera feed over the broker
    #ifdef __SMCE__
      Camera.begin(QVGA, RGB888, 15);
      frameBuffer.resize(Camera.width() * Camera.height() * Camera.bytesPerPixel());
    #endif

      WiFi.begin(ssid, pass);
      mqtt.begin(mqttBrokerUrl, 1883, net);

      Serial.println("Connecting to WiFi...");
      auto wifiStatus = WiFi.status();
      while (wifiStatus != WL_CONNECTED && wifiStatus != WL_NO_SHIELD) {
        Serial.println(wifiStatus);
        Serial.print(".");
        delay(1000);
        wifiStatus = WiFi.status();
      }


      Serial.println("Connecting to MQTT broker");
      while (!mqtt.connect("arduino", "public", "public")) {
        Serial.print(".");
        delay(1000);
      }
       //Subscribes to the following broker channels
      mqtt.subscribe("/smartcar/control/#", 1);

      //interprets broker messages from subscribed channels
      mqtt.onMessage([](String topic, String message) {

        if (topic == "/smartcar/control/throttle") {
           if (frontDanger == true && message.toInt() > 0 ) {
               currentThrottle = 0;
               car.setSpeed(currentThrottle);
            } else {
             currentThrottle = message.toInt();
             car.setSpeed(currentThrottle);
            }
        } else if (topic == "/smartcar/control/steering") {
            currentSteeringAngle = message.toInt();
            if (currentSteeringAngle < 0) {
            car.overrideMotorSpeed(-50, 50);
            } else if (currentSteeringAngle > 0) {
            car.overrideMotorSpeed(50, -50);
            }
        } else {
          Serial.println(topic + " " + message);
        }
      });
    }

//Handles input through the serial port
void handleInput()
{
    if(Serial.available())
    {
        char input = Serial.read();

        switch (input)
        {
            case 'l':
                car.setSpeed(forwardSpeed);
                car.setAngle(leftDegrees);
                movingBackwards = false;
                break;
            case 'r':
                car.setSpeed(forwardSpeed);
                car.setAngle(rightDegrees);
                movingBackwards = false;
                break;
            case 'f':
                car.setSpeed(forwardSpeed);
                car.setAngle(0);
                movingBackwards = false;
                break;
            case 'b':
                car.setSpeed(reverseSpeed);
                car.setAngle(0);
                movingBackwards = true;
                break;
            case 's':
                car.setSpeed(0);
                car.setAngle(0);
                movingBackwards = false;
            default:
                car.setSpeed(0);
                car.setAngle(0);
                movingBackwards = false;
        }
    }
}
//main loop that runs repetedly
void loop() {
  handleInput();            //car controls via serial

  if (mqtt.connected()) {
      mqtt.loop();
      const auto currentTime = millis();
  #ifdef __SMCE__
      static auto previousFrame = 0UL;
      if (currentTime - previousFrame >= 65) {
        previousFrame = currentTime;
        Camera.readFrame(frameBuffer.data());
        mqtt.publish("/smartcar/camera", frameBuffer.data(), frameBuffer.size(),
                     false, 0);
      }
  #endif
      static auto previousTransmission = 0UL;
      if (currentTime - previousTransmission >= oneSecond) {
        previousTransmission = currentTime;
        auto frontDistance = front.getDistance();
        // auto backDistance = rear.getDistance(); //for future reverse obstacle detection


           // handles obstacle detection when steering over android
           if(frontDistance < distanceToObject && frontDistance > 0 && currentThrottle > 0 && currentSteeringAngle == 0) {
                frontDanger = true;
                car.setSpeed(0);
                currentThrottle = 0;
           } else {
                frontDanger = false;
           }
            /*
              // for future reverse obstacle detection
           if(rearDistanceDistance < distanceToObject && rearDistance > 0) {
                   rearDanger = true;
           } else {
                   rearDanger = false;
           }
           */
                //publishes front distance to broker, not currently used
                mqtt.publish("/smartcar/ultrasound/front", String(frontDistance));
      }
    }
  #ifdef __SMCE__
    delay(1);                 //delay for emulators sake
  #endif
  }

