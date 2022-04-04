#include <Smartcar.h>

const int forwardSpeed = 70; // 70% of the full speed forward
const int reverseSpeed   = -70; // 70% of the full speed backward
const int leftDegrees = -75; // degrees to turn left
const int rightDegrees = 75;  // degrees to turn right



// Copied from Dimitrios blog
ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);

SimpleCar car(control);

void setup() {
  // put your setup code here, to run once:
    Serial.begin(9600); //starts and waits for some time to sensors to start
}
void loop() {
  // put your main code here, to run repeatedly:
  delay(1);
  handleInput();

}

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
                break;
            case 'r':
                car.setSpeed(forwardSpeed);
                car.setAngle(rightDegrees);
                break;
            case 'f':
                car.setSpeed(forwardSpeed);
                car.setAngle(0);
                break;
            case 'b':
                car.setSpeed(reverseSpeed);
                car.setAngle(0);
                break;
            default:
                car.setSpeed(0);
                car.setAngle(0);
        }
    }
}
