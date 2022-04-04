#include <Smartcar.h>

void setup() {
  // put your setup code here, to run once:
}
void loop() {
  // put your main code here, to run repeatedly:
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
