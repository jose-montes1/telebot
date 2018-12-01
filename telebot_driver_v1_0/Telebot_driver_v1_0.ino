#include <SoftwareSerial.h>


/*Commands:
R|<int> - this sets the speed of the right motor to the value in int. (-100 to 100)
L|<int> - this sets the speed of the left motor to the value in int. (-100 to 100)
*/

//Define declarations
//motor pwm pin
#define LEFT_PWM_PIN 5
#define RIGHT_PWM_PIN 6

//motor direction pin
#define LEFT_DIRECTION_PIN 8
#define RIGHT_DIRECTION_PIN 7

//Motor Direction Declarations
#define FORWARDS 1
#define BACKWARDS 0

//Variable declarations
char input_byte;
String command_string;
int left_motor_speed = 0;
int right_motor_speed = 0;
boolean debug_mode = false;
boolean clear_string = false;

//Abstracting the pins
void setMotorSpeed(int speeds, int motorPin){
  //analogWrite(motorPin, (byte) ((long) 255*speeds)/100);
  if(speeds > 0){
    digitalWrite(motorPin, HIGH);
  }else{
    digitalWrite(motorPin, LOW);
  }
}

void setMotorDirection(int direction, int motorPin){
  digitalWrite(motorPin, direction); 
}

void update_motor_speed(){
  if(left_motor_speed >= 0){
    setMotorDirection(FORWARDS, LEFT_DIRECTION_PIN);
  } else {
    setMotorDirection(BACKWARDS,LEFT_DIRECTION_PIN);
  }
  
  if(right_motor_speed >= 0){
    setMotorDirection(FORWARDS, RIGHT_DIRECTION_PIN);
  } else {
    setMotorDirection(BACKWARDS,RIGHT_DIRECTION_PIN);
  }
  
  int temp_left_motor_speed = abs(left_motor_speed);
  int temp_right_motor_speed = abs(right_motor_speed);
  
  if(temp_left_motor_speed > 100){
    temp_left_motor_speed = 100;
  }
  if(temp_right_motor_speed > 100){
    temp_right_motor_speed = 100;
  }
  
  setMotorSpeed(left_motor_speed, LEFT_PWM_PIN);
  setMotorSpeed(right_motor_speed, RIGHT_PWM_PIN);
}

void setup(){
  
  //Initialize serial channels
  //for debugging purposes only
  Serial.begin(57600);
  
  //Initialize pin outputs
  //motor as output
  pinMode(LEFT_PWM_PIN, OUTPUT);
  pinMode(RIGHT_PWM_PIN, OUTPUT);
  pinMode(LEFT_DIRECTION_PIN, OUTPUT);
  pinMode(RIGHT_DIRECTION_PIN, OUTPUT);
}

void loop(){
  //Reset string for new command
  command_string = "";
  
  //Receive new command 
  while(Serial.available() > 0){
    input_byte = (char) Serial.read();
    if(input_byte == ':'){
      break;
    }else{
      command_string += input_byte;
    }
    delay(10);
  }
  
  
  
  //Process new command
  if(command_string.length() > 0){
    switch(command_string.charAt(0)){
      case 'L':
        left_motor_speed = command_string.substring(2).toInt();
        update_motor_speed();
        break;
      case 'R':
        right_motor_speed = command_string.substring(2).toInt();
        update_motor_speed();
        break;
      case 'D':
        if(debug_mode == true){
          debug_mode = false;
        }else{
          debug_mode = true;
        }
        break;
      default:
        break;
    }
  }
  
  if(debug_mode){
    Serial.print("Right motor speed: ");
    Serial.print(right_motor_speed);
    Serial.print(" Left motor speed: ");
    Serial.print(left_motor_speed);
    Serial.print(" Command string: ");
    Serial.print(command_string);
    Serial.println("~");
  }
}



//String sendString = "R|" + (carSpeed) +":L|" + (carSpeed) + ":";
//R|500:L|-400
//R|50:L|-40
//R|4:L|10
