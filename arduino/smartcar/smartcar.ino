#include <Smartcar.h>

const int forwardSpeed = 70; // 70% of the full speed forward
const int reverseSpeed   = -70; // 70% of the full speed backward
const int leftDegrees = -75; // degrees to turn left
const int rightDegrees = 75;  // degrees to turn right
const int distanceToObject = 70; //distance to object when the car stops
bool reverse = false;

// Copied from Dimitrios blog, initiates parts of the car
ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);

SimpleCar car(control);

const int triggerPin           = 6; // D6
const int echoPin              = 7; // D7
const unsigned int maxDistance = 100;

SR04 front{arduinoRuntime, triggerPin, echoPin, maxDistance};

void setup() {
  // put your setup code here, to run once:
    Serial.begin(9600); //starts and waits for some time to sensors to start
}
void loop() {
  // put your main code here, to run repeatedly:

  handleInput();            //car controls
  obstacleDetection();      //obstacle detection
  delay(1);                 //delay for emulators sake
  //Serial.println(front.getDistance()); //if we want to output the front distance in serial
}

//Obstacle detection through the ultrasonic sensor. Milestone says to stop 15cm from an obstacle
void obstacleDetection()
{
    const auto distance = front.getDistance();
    if(reverse == false ) {
        if (distance > 0 && distance < distanceToObject) {
            car.setSpeed(0);
        }
    }
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
                reverse = false;
                break;
            case 'r':
                car.setSpeed(forwardSpeed);
                car.setAngle(rightDegrees);
                reverse = false;
                break;
            case 'f':
                car.setSpeed(forwardSpeed);
                car.setAngle(0);
                reverse = false;
                break;
            case 'b':
                car.setSpeed(reverseSpeed);
                car.setAngle(0);
                reverse = true;
                break;
            default:
                car.setSpeed(0);
                car.setAngle(0);
                reverse = false;
				break;
        }
    }
}
