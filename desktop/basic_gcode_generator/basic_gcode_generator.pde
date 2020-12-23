
void setup() {
  size(800,600);
  rectMode(CENTER);
  /*
  rect(100,100,20,100);
  ellipse(100,70,60,60);
  ellipse(81,70,16,32); 
  ellipse(119,70,16,32); 
  line(90,150,80,160);
  line(110,150,120,160);
  */
  
  int feedRate = 2400;
  float circleDiameter = 50;
  float width = 1067;
  
  PVector home = new PVector(533, 300);
  PVector offset = new PVector(571, 571);
  
  PrintWriter writer = createWriter("circle.gcode");
  writer.println("G90 ; Absolute positioning\n");
  
  for (float t = 0; t < 2 * PI; t += PI / 300.0) {
    PVector nextXY = new PVector(circleDiameter * cos(t) + home.x, circleDiameter * sin(t) + home.y);
    PVector nextLR = new PVector(sqrt(sq(nextXY.x) + sq(nextXY.y)), sqrt(sq(width - nextXY.x) + sq(nextXY.y)));
    writer.println(String.format("G01 F%d X%f Y%f", feedRate, (nextLR.x - offset.x), -(nextLR.y - offset.y)));
  }
  
  writer.flush();
  writer.close();
}

void draw() {
}
