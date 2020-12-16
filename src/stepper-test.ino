const int kDirPin0 = 21;
const int kStepPin0 = 22;

void setup() {
  pinMode(kDirPin0, OUTPUT);
  pinMode(kStepPin0, OUTPUT);

  digitalWrite(kDirPin0, HIGH);
}

bool dir = true;
uint32_t flip_at = 0;

void loop() {
  if (millis() > flip_at) {
    digitalWrite(kDirPin0, dir);
    dir = !dir;
    flip_at = millis() + 1000;
  }

  digitalWrite(kStepPin0, HIGH);
  delayMicroseconds(10);
  digitalWrite(kStepPin0, LOW);
  delay(2);
}
