use <third_party/openscad-fillets/fillets2d.scad>

// Design for an electrostatic adhesion plate. Generates the negative, i.e. the part to cut out of a conductive material.

$fn = 50;

scale = 6;
// See https://iopscience.iop.org/article/10.1088/0022-3727/49/41/415304/meta
finger_width = 1.9 * scale;
finger_fillet = scale / 2 - .01;
gap_width = 1.0 * scale;
unit_width = (finger_width + gap_width) * 2;

target_width = 100;
width = ceil(target_width / unit_width) * unit_width;
height = 100;
border = 5;

module board() {
    color("red") {
        //square([width, height]);
    }
    
    fillet2d(finger_fillet) {
        rounding2d(finger_fillet) {
            for (i = [0 : unit_width : target_width]) {
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
                    translate([finger_width + gap_width, height - gap_width]) {
                        square([finger_width + gap_width * 2, gap_width]);
                    }
                }
            }
        }
    }

    fillet2d(border / 2 - .01) {
        difference() {
            translate([-border + finger_width / 4, -border + finger_width / 2]) {
                square([width + border * 2, height + border * 2]);
            }
            translate([finger_width / 4, finger_width / 2]) {
                square([width, height]);
            }
        }
    }
}

board();