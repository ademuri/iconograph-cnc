use <third_party/openscad-fillets/fillets2d.scad>

// Design for an electrostatic adhesion plate. Generates the negative, i.e. the part to cut out of a conductive material.

$fn = 50;

width = 100;
height = 100;
finger_width = 0.75;
finger_fillet = 0.3;
gap_width = 1.0;

unit_width = (finger_width + gap_width) * 2;

module board() {
    color("red") {
        //square([width, height]);
    }
    
    fillet2d(finger_fillet) {
        rounding2d(finger_fillet) {
            for (i = [0 : unit_width : width]) {
                translate([i, 0]) {
                    translate([0, finger_width]) {
                        if (i == 0) {
                            square([finger_width + gap_width, gap_width]);
                        } else {
                            square([gap_width, height - (finger_width + gap_width)]);
                            square([finger_width + gap_width * 2, gap_width]);
                        }
                    }
                    translate([finger_width + gap_width, finger_width]) {
                        square([gap_width, height - (finger_width + gap_width)]);
                    }
                    translate([finger_width + gap_width, height - finger_width - gap_width]) {
                        square([finger_width + gap_width * 2, finger_width]);
                    }
                }
            }
        }
    }
}

board();