#include <vector>

#include <MQTT.h>
#include <WiFi.h>
#ifdef __SMCE__
#include <OV767X.h>
#endif

#include <Smartcar.h>

const int forwardSpeed = 70; // 70% of the full speed forward
const int reverseSpeed   = -70; // 70% of the full speed backward
const int leftDegrees = -75; // degrees to turn left
const int rightDegrees = 75;  // degrees to turn right
const auto distanceToObject = 75; //distance to object when the car stops
bool movingBackwards = false;
bool ableToMove = true;


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

const auto oneSecond = 100UL;
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

      mqtt.subscribe("/smartcar/control/#", 1);
      mqtt.onMessage([](String topic, String message) {
        if (topic == "/smartcar/control/throttle") {
          car.setSpeed(message.toInt());
        } else if (topic == "/smartcar/control/steering") {
          car.setAngle(message.toInt());
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
void loop() {
  // put your main code here, to run repeatedly:

  handleInput();            //car controls
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
        float CurrentSpeed = car.getSpeed();
        const auto distance = front.getDistance();
         /* if (distance > 0 && distance < distanceToObject) { //Obstacle detection through the ultrasonic sensor.
            if(ableToMove) {
                car.setSpeed(0);
                Serial.println("Obstacle successfully avoided.");
                }
                ableToMove = false;
            } else {
                ableToMove = true;
                } */
                   
          if(distance < distanceToObject && distance > 0) {
            if (CurrentSpeed > 0) {
              car.setSpeed(0);
              Serial.println("You are in the danger zone");
            }
            if (CurrentSpeed == 0) {
               car.setSpeed(-1);
              }

           /* const auto frontDistance = front.getDistance();
            const auto rearDistance = rear.getDistance();
              if(frontDistance < distanceToObject && frontDistance > 0 && car.getSpeed > 0 && car.getSpeed < 90) { //allows for reverse and rotation above 90 throttle
                car.setSpeed(0);
                Serial.println("You are in the danger zone")
                Serial.println(front.getDistance()); //if we want to output the front distance in serial
                }
              if(rearDistance < distanceToObject && rearDistance > 0 && car.getSpeed < 0 && car.getSpeed > 90) { //allows for forward and rotation above 90 throttle
                car.setSpeed(0);
                Serial.println("You are in the rear danger zone")
                Serial.println(rear.getDistance()); //if we want to output the rear distance in serial
                }*/
                mqtt.publish("/smartcar/ultrasound/front", String(distance));
      }
    }
  #ifdef __SMCE__
    delay(1);                 //delay for emulators sake
  #endif
  }
} 
