EESchema Schematic File Version 4
EELAYER 30 0
EELAYER END
$Descr A3 16535 11693
encoding utf-8
Sheet 1 1
Title ""
Date ""
Rev ""
Comp ""
Comment1 ""
Comment2 ""
Comment3 ""
Comment4 ""
$EndDescr
$Comp
L ESP32_mini_KiCad_Library:mini_esp32 U1
U 1 1 5FDAECBB
P 1850 2650
F 0 "U1" H 1875 2775 50  0000 C CNN
F 1 "mini_esp32" H 1875 2684 50  0000 C CNN
F 2 "snapeda:ESP32_mini" H 2000 2750 50  0001 C CNN
F 3 "" H 2000 2750 50  0001 C CNN
	1    1850 2650
	-1   0    0    -1  
$EndComp
$Comp
L Driver_Motor:Pololu_Breakout_DRV8825 A1
U 1 1 5FDAFAF7
P 11200 1700
F 0 "A1" H 10950 950 50  0000 C CNN
F 1 "Pololu_Breakout_DRV8825" H 11850 950 50  0000 C CNN
F 2 "Module:Pololu_Breakout-16_15.2x20.3mm" H 11400 900 50  0001 L CNN
F 3 "https://www.pololu.com/product/2982" H 11300 1400 50  0001 C CNN
	1    11200 1700
	1    0    0    -1  
$EndComp
$Comp
L Homebrew:Anderson_two_redblack_src J3
U 1 1 5FDB1952
P 1650 1050
F 0 "J3" H 1650 1200 60  0000 C CNN
F 1 "Anderson_two_redblack_src" H 2100 900 60  0000 C CNN
F 2 "homebrew:Anderson_1336G1_right_angle_bottom_two" H 1600 900 60  0001 C CNN
F 3 "" H 1600 900 60  0001 C CNN
	1    1650 1050
	-1   0    0    -1  
$EndComp
$Comp
L power:+5V #PWR05
U 1 1 5FDB39BE
P 1250 2450
F 0 "#PWR05" H 1250 2300 50  0001 C CNN
F 1 "+5V" H 1265 2623 50  0000 C CNN
F 2 "" H 1250 2450 50  0001 C CNN
F 3 "" H 1250 2450 50  0001 C CNN
	1    1250 2450
	-1   0    0    -1  
$EndComp
Wire Wire Line
	1300 2750 1250 2750
Wire Wire Line
	1250 2750 1250 2450
$Comp
L power:+5V #PWR021
U 1 1 5FDB64BE
P 10750 1250
F 0 "#PWR021" H 10750 1100 50  0001 C CNN
F 1 "+5V" H 10765 1423 50  0000 C CNN
F 2 "" H 10750 1250 50  0001 C CNN
F 3 "" H 10750 1250 50  0001 C CNN
	1    10750 1250
	1    0    0    -1  
$EndComp
Wire Wire Line
	10750 1250 10750 1300
Wire Wire Line
	10750 1300 10800 1300
$Comp
L power:VCC #PWR07
U 1 1 5FDB97E5
P 2050 950
F 0 "#PWR07" H 2050 800 50  0001 C CNN
F 1 "VCC" H 2065 1123 50  0000 C CNN
F 2 "" H 2050 950 50  0001 C CNN
F 3 "" H 2050 950 50  0001 C CNN
	1    2050 950 
	1    0    0    -1  
$EndComp
Wire Wire Line
	2000 1000 2050 1000
Wire Wire Line
	2050 1000 2050 950 
$Comp
L power:GND #PWR08
U 1 1 5FDBA3D3
P 2050 1150
F 0 "#PWR08" H 2050 900 50  0001 C CNN
F 1 "GND" H 2055 977 50  0000 C CNN
F 2 "" H 2050 1150 50  0001 C CNN
F 3 "" H 2050 1150 50  0001 C CNN
	1    2050 1150
	1    0    0    -1  
$EndComp
Wire Wire Line
	2000 1100 2050 1100
Wire Wire Line
	2050 1100 2050 1150
$Comp
L power:VCC #PWR022
U 1 1 5FDBAFFA
P 11200 650
F 0 "#PWR022" H 11200 500 50  0001 C CNN
F 1 "VCC" H 11215 823 50  0000 C CNN
F 2 "" H 11200 650 50  0001 C CNN
F 3 "" H 11200 650 50  0001 C CNN
	1    11200 650 
	1    0    0    -1  
$EndComp
$Comp
L Device:CP C2
U 1 1 5FDBB8D9
P 11650 900
F 0 "C2" H 11768 946 50  0000 L CNN
F 1 "100uF" H 11768 855 50  0000 L CNN
F 2 "Capacitor_THT:CP_Radial_D8.0mm_P3.50mm" H 11688 750 50  0001 C CNN
F 3 "~" H 11650 900 50  0001 C CNN
	1    11650 900 
	1    0    0    -1  
$EndComp
Wire Wire Line
	11200 650  11200 750 
$Comp
L power:GND #PWR025
U 1 1 5FDBBF53
P 11650 1050
F 0 "#PWR025" H 11650 800 50  0001 C CNN
F 1 "GND" H 11655 877 50  0000 C CNN
F 2 "" H 11650 1050 50  0001 C CNN
F 3 "" H 11650 1050 50  0001 C CNN
	1    11650 1050
	1    0    0    -1  
$EndComp
Wire Wire Line
	11650 750  11200 750 
Connection ~ 11200 750 
Wire Wire Line
	11200 750  11200 1100
$Comp
L power:GND #PWR023
U 1 1 5FDBCBF7
P 11200 2550
F 0 "#PWR023" H 11200 2300 50  0001 C CNN
F 1 "GND" H 11205 2377 50  0000 C CNN
F 2 "" H 11200 2550 50  0001 C CNN
F 3 "" H 11200 2550 50  0001 C CNN
	1    11200 2550
	1    0    0    -1  
$EndComp
Wire Wire Line
	11200 2550 11200 2500
Wire Wire Line
	11300 2550 11300 2500
Wire Wire Line
	10750 1300 10750 1400
Wire Wire Line
	10750 1400 10800 1400
Connection ~ 10750 1300
Wire Wire Line
	10750 1400 10750 1500
Wire Wire Line
	10750 1500 10800 1500
Connection ~ 10750 1400
Text GLabel 10800 1800 0    50   Input ~ 0
STEP0
Text GLabel 10800 1900 0    50   Input ~ 0
DIR0
NoConn ~ 10800 1700
$Comp
L Connector_Generic:Conn_01x04 J11
U 1 1 5FDBF731
P 12000 1800
F 0 "J11" H 12080 1792 50  0000 L CNN
F 1 "Conn_01x04" H 12080 1701 50  0000 L CNN
F 2 "Connector_PinHeader_2.54mm:PinHeader_1x04_P2.54mm_Vertical" H 12000 1800 50  0001 C CNN
F 3 "~" H 12000 1800 50  0001 C CNN
	1    12000 1800
	1    0    0    -1  
$EndComp
Wire Wire Line
	11600 1600 11800 1600
Wire Wire Line
	11800 1600 11800 1700
Wire Wire Line
	11600 1700 11700 1700
Wire Wire Line
	11700 1700 11700 1800
Wire Wire Line
	11700 1800 11800 1800
Wire Wire Line
	11600 1900 11800 1900
Wire Wire Line
	11600 2000 11800 2000
$Comp
L Switch:SW_DIP_x03 SW1
U 1 1 5FDC0F13
P 10150 2300
F 0 "SW1" H 10150 2767 50  0000 C CNN
F 1 "SW_DIP_x03" H 10150 2676 50  0000 C CNN
F 2 "Button_Switch_THT:SW_DIP_SPSTx03_Slide_9.78x9.8mm_W7.62mm_P2.54mm" H 10150 2300 50  0001 C CNN
F 3 "~" H 10150 2300 50  0001 C CNN
	1    10150 2300
	1    0    0    -1  
$EndComp
$Comp
L power:+5V #PWR017
U 1 1 5FDC1C6E
P 9750 2050
F 0 "#PWR017" H 9750 1900 50  0001 C CNN
F 1 "+5V" H 9765 2223 50  0000 C CNN
F 2 "" H 9750 2050 50  0001 C CNN
F 3 "" H 9750 2050 50  0001 C CNN
	1    9750 2050
	1    0    0    -1  
$EndComp
Wire Wire Line
	9750 2050 9750 2100
Wire Wire Line
	9750 2100 9850 2100
Wire Wire Line
	9750 2100 9750 2200
Wire Wire Line
	9750 2200 9850 2200
Connection ~ 9750 2100
Wire Wire Line
	9750 2200 9750 2300
Wire Wire Line
	9750 2300 9850 2300
Connection ~ 9750 2200
Wire Wire Line
	10450 2300 10800 2300
Wire Wire Line
	10450 2200 10800 2200
Wire Wire Line
	10450 2100 10800 2100
$Comp
L Driver_Motor:Pololu_Breakout_DRV8825 A3
U 1 1 5FDCCDE3
P 14500 1700
F 0 "A3" H 14250 950 50  0000 C CNN
F 1 "Pololu_Breakout_DRV8825" H 15150 950 50  0000 C CNN
F 2 "Module:Pololu_Breakout-16_15.2x20.3mm" H 14700 900 50  0001 L CNN
F 3 "https://www.pololu.com/product/2982" H 14600 1400 50  0001 C CNN
	1    14500 1700
	1    0    0    -1  
$EndComp
$Comp
L power:+5V #PWR031
U 1 1 5FDCCF89
P 14050 1250
F 0 "#PWR031" H 14050 1100 50  0001 C CNN
F 1 "+5V" H 14065 1423 50  0000 C CNN
F 2 "" H 14050 1250 50  0001 C CNN
F 3 "" H 14050 1250 50  0001 C CNN
	1    14050 1250
	1    0    0    -1  
$EndComp
Wire Wire Line
	14050 1250 14050 1300
Wire Wire Line
	14050 1300 14100 1300
$Comp
L power:VCC #PWR034
U 1 1 5FDCCF95
P 14500 650
F 0 "#PWR034" H 14500 500 50  0001 C CNN
F 1 "VCC" H 14515 823 50  0000 C CNN
F 2 "" H 14500 650 50  0001 C CNN
F 3 "" H 14500 650 50  0001 C CNN
	1    14500 650 
	1    0    0    -1  
$EndComp
$Comp
L Device:CP C4
U 1 1 5FDCCF9F
P 14950 900
F 0 "C4" H 15068 946 50  0000 L CNN
F 1 "100uF" H 15068 855 50  0000 L CNN
F 2 "Capacitor_THT:CP_Radial_D8.0mm_P3.50mm" H 14988 750 50  0001 C CNN
F 3 "~" H 14950 900 50  0001 C CNN
	1    14950 900 
	1    0    0    -1  
$EndComp
Wire Wire Line
	14500 650  14500 750 
$Comp
L power:GND #PWR037
U 1 1 5FDCCFAA
P 14950 1050
F 0 "#PWR037" H 14950 800 50  0001 C CNN
F 1 "GND" H 14955 877 50  0000 C CNN
F 2 "" H 14950 1050 50  0001 C CNN
F 3 "" H 14950 1050 50  0001 C CNN
	1    14950 1050
	1    0    0    -1  
$EndComp
Wire Wire Line
	14950 750  14500 750 
Connection ~ 14500 750 
Wire Wire Line
	14500 750  14500 1100
$Comp
L power:GND #PWR035
U 1 1 5FDCCFB7
P 14500 2550
F 0 "#PWR035" H 14500 2300 50  0001 C CNN
F 1 "GND" H 14505 2377 50  0000 C CNN
F 2 "" H 14500 2550 50  0001 C CNN
F 3 "" H 14500 2550 50  0001 C CNN
	1    14500 2550
	1    0    0    -1  
$EndComp
Wire Wire Line
	14500 2550 14500 2500
Wire Wire Line
	14600 2550 14600 2500
Wire Wire Line
	14050 1300 14050 1400
Wire Wire Line
	14050 1400 14100 1400
Connection ~ 14050 1300
Wire Wire Line
	14050 1400 14050 1500
Wire Wire Line
	14050 1500 14100 1500
Connection ~ 14050 1400
Text GLabel 14100 1800 0    50   Input ~ 0
STEP1
Text GLabel 14100 1900 0    50   Input ~ 0
DIR1
NoConn ~ 14100 1700
$Comp
L Connector_Generic:Conn_01x04 J13
U 1 1 5FDCCFD6
P 15300 1800
F 0 "J13" H 15380 1792 50  0000 L CNN
F 1 "Conn_01x04" H 15380 1701 50  0000 L CNN
F 2 "Connector_PinHeader_2.54mm:PinHeader_1x04_P2.54mm_Vertical" H 15300 1800 50  0001 C CNN
F 3 "~" H 15300 1800 50  0001 C CNN
	1    15300 1800
	1    0    0    -1  
$EndComp
Wire Wire Line
	14900 1600 15100 1600
Wire Wire Line
	15100 1600 15100 1700
Wire Wire Line
	14900 1700 15000 1700
Wire Wire Line
	15000 1700 15000 1800
Wire Wire Line
	15000 1800 15100 1800
Wire Wire Line
	14900 1900 15100 1900
Wire Wire Line
	14900 2000 15100 2000
$Comp
L Switch:SW_DIP_x03 SW3
U 1 1 5FDCCFE7
P 13450 2300
F 0 "SW3" H 13450 2767 50  0000 C CNN
F 1 "SW_DIP_x03" H 13450 2676 50  0000 C CNN
F 2 "Button_Switch_THT:SW_DIP_SPSTx03_Slide_9.78x9.8mm_W7.62mm_P2.54mm" H 13450 2300 50  0001 C CNN
F 3 "~" H 13450 2300 50  0001 C CNN
	1    13450 2300
	1    0    0    -1  
$EndComp
$Comp
L power:+5V #PWR029
U 1 1 5FDCCFF1
P 13050 2050
F 0 "#PWR029" H 13050 1900 50  0001 C CNN
F 1 "+5V" H 13065 2223 50  0000 C CNN
F 2 "" H 13050 2050 50  0001 C CNN
F 3 "" H 13050 2050 50  0001 C CNN
	1    13050 2050
	1    0    0    -1  
$EndComp
Wire Wire Line
	13050 2050 13050 2100
Wire Wire Line
	13050 2100 13150 2100
Wire Wire Line
	13050 2100 13050 2200
Wire Wire Line
	13050 2200 13150 2200
Connection ~ 13050 2100
Wire Wire Line
	13050 2200 13050 2300
Wire Wire Line
	13050 2300 13150 2300
Connection ~ 13050 2200
Wire Wire Line
	13750 2300 14100 2300
Wire Wire Line
	13750 2200 14100 2200
Wire Wire Line
	13750 2100 14100 2100
$Comp
L power:GND #PWR01
U 1 1 5FDEA4DA
P 900 3150
F 0 "#PWR01" H 900 2900 50  0001 C CNN
F 1 "GND" H 905 2977 50  0000 C CNN
F 2 "" H 900 3150 50  0001 C CNN
F 3 "" H 900 3150 50  0001 C CNN
	1    900  3150
	1    0    0    -1  
$EndComp
Wire Wire Line
	900  3150 1250 3150
Wire Wire Line
	1300 3050 1250 3050
Connection ~ 1250 3150
Wire Wire Line
	1250 3150 1300 3150
Wire Wire Line
	1250 2950 1300 2950
Wire Wire Line
	1250 2950 1250 3050
Connection ~ 1250 3050
Wire Wire Line
	1250 3050 1250 3150
$Comp
L power:+3V3 #PWR02
U 1 1 5FDEE51D
P 1050 2800
F 0 "#PWR02" H 1050 2650 50  0001 C CNN
F 1 "+3V3" H 1065 2973 50  0000 C CNN
F 2 "" H 1050 2800 50  0001 C CNN
F 3 "" H 1050 2800 50  0001 C CNN
	1    1050 2800
	1    0    0    -1  
$EndComp
Wire Wire Line
	1050 2800 1050 2850
Wire Wire Line
	1050 2850 1300 2850
NoConn ~ 1300 3750
NoConn ~ 1300 3850
NoConn ~ 1300 3950
NoConn ~ 1300 4050
NoConn ~ 1300 4150
NoConn ~ 1300 4250
NoConn ~ 1300 4350
NoConn ~ 1300 3250
Text GLabel 2350 5050 2    50   Input ~ 0
STEP0
Text GLabel 2350 4750 2    50   Input ~ 0
DIR0
Text GLabel 2350 3750 2    50   Input ~ 0
STEP1
Text GLabel 2350 3650 2    50   Input ~ 0
DIR1
$Comp
L Transistor_Array:ULN2003 U2
U 1 1 5FDFEF7F
P 13000 7950
F 0 "U2" H 13000 8617 50  0000 C CNN
F 1 "ULN2003" H 13000 8526 50  0000 C CNN
F 2 "Package_DIP:DIP-16_W7.62mm" H 13050 7400 50  0001 L CNN
F 3 "http://www.ti.com/lit/ds/symlink/uln2003a.pdf" H 13100 7750 50  0001 C CNN
	1    13000 7950
	1    0    0    -1  
$EndComp
$Comp
L power:+5V #PWR015
U 1 1 5FE00D26
P 13450 7050
F 0 "#PWR015" H 13450 6900 50  0001 C CNN
F 1 "+5V" H 13465 7223 50  0000 C CNN
F 2 "" H 13450 7050 50  0001 C CNN
F 3 "" H 13450 7050 50  0001 C CNN
	1    13450 7050
	-1   0    0    -1  
$EndComp
Wire Wire Line
	13450 7550 13400 7550
$Comp
L Device:CP C1
U 1 1 5FE0384D
P 13750 7300
F 0 "C1" H 13868 7346 50  0000 L CNN
F 1 "100uF" H 13868 7255 50  0000 L CNN
F 2 "Capacitor_THT:CP_Radial_D8.0mm_P3.50mm" H 13788 7150 50  0001 C CNN
F 3 "~" H 13750 7300 50  0001 C CNN
	1    13750 7300
	1    0    0    -1  
$EndComp
Wire Wire Line
	13450 7050 13450 7100
Wire Wire Line
	13450 7100 13750 7100
Wire Wire Line
	13750 7100 13750 7150
Wire Wire Line
	13450 7100 13450 7550
$Comp
L power:GND #PWR016
U 1 1 5FE085E7
P 13750 7450
F 0 "#PWR016" H 13750 7200 50  0001 C CNN
F 1 "GND" H 13755 7277 50  0000 C CNN
F 2 "" H 13750 7450 50  0001 C CNN
F 3 "" H 13750 7450 50  0001 C CNN
	1    13750 7450
	1    0    0    -1  
$EndComp
Connection ~ 13450 7100
$Comp
L power:GND #PWR014
U 1 1 5FE0E6E4
P 13000 8550
F 0 "#PWR014" H 13000 8300 50  0001 C CNN
F 1 "GND" H 13005 8377 50  0000 C CNN
F 2 "" H 13000 8550 50  0001 C CNN
F 3 "" H 13000 8550 50  0001 C CNN
	1    13000 8550
	1    0    0    -1  
$EndComp
Text GLabel 12600 7750 0    50   Input ~ 0
T0_SIGNAL
Text GLabel 12600 7850 0    50   Input ~ 0
T1_SIGNAL
Text GLabel 12600 7950 0    50   Input ~ 0
T2_SIGNAL
Text GLabel 12600 8050 0    50   Input ~ 0
T3_SIGNAL
Text GLabel 12600 8150 0    50   Input ~ 0
T4_SIGNAL
Text GLabel 12600 8250 0    50   Input ~ 0
T5_SIGNAL
$Comp
L Connector_Generic:Conn_01x05 J7
U 1 1 5FE11580
P 14400 7950
F 0 "J7" H 14480 7992 50  0000 L CNN
F 1 "Conn_01x05" H 14480 7901 50  0000 L CNN
F 2 "Connector_PinHeader_2.54mm:PinHeader_1x05_P2.54mm_Vertical" H 14400 7950 50  0001 C CNN
F 3 "~" H 14400 7950 50  0001 C CNN
	1    14400 7950
	1    0    0    -1  
$EndComp
Wire Wire Line
	13400 7750 14200 7750
Wire Wire Line
	14200 7850 13400 7850
Wire Wire Line
	13400 7950 14200 7950
Wire Wire Line
	14200 8050 13400 8050
$Comp
L power:+5V #PWR020
U 1 1 5FE18728
P 14650 8300
F 0 "#PWR020" H 14650 8150 50  0001 C CNN
F 1 "+5V" H 14665 8473 50  0000 C CNN
F 2 "" H 14650 8300 50  0001 C CNN
F 3 "" H 14650 8300 50  0001 C CNN
	1    14650 8300
	-1   0    0    -1  
$EndComp
Wire Wire Line
	14650 8300 14150 8300
Wire Wire Line
	14150 8300 14150 8150
Wire Wire Line
	14150 8150 14200 8150
Text GLabel 13400 8150 2    50   Input ~ 0
T4_OUT
Text GLabel 13400 8250 2    50   Input ~ 0
T5_OUT
$Comp
L Connector_Generic:Conn_01x02 J8
U 1 1 5FE1C605
P 14500 8750
F 0 "J8" H 14580 8742 50  0000 L CNN
F 1 "Conn_01x02" H 14580 8651 50  0000 L CNN
F 2 "homebrew:Wurth_ScrewTerminal_1x02_P5.00mm_691137710002" H 14500 8750 50  0001 C CNN
F 3 "~" H 14500 8750 50  0001 C CNN
	1    14500 8750
	1    0    0    -1  
$EndComp
$Comp
L power:+5V #PWR018
U 1 1 5FE1D279
P 14250 8700
F 0 "#PWR018" H 14250 8550 50  0001 C CNN
F 1 "+5V" H 14265 8873 50  0000 C CNN
F 2 "" H 14250 8700 50  0001 C CNN
F 3 "" H 14250 8700 50  0001 C CNN
	1    14250 8700
	-1   0    0    -1  
$EndComp
Wire Wire Line
	14250 8700 14250 8750
Wire Wire Line
	14250 8750 14300 8750
Text GLabel 14300 8850 0    50   Input ~ 0
T4_OUT
$Comp
L Connector_Generic:Conn_01x02 J9
U 1 1 5FE22FC6
P 14500 9300
F 0 "J9" H 14580 9292 50  0000 L CNN
F 1 "Conn_01x02" H 14580 9201 50  0000 L CNN
F 2 "homebrew:Wurth_ScrewTerminal_1x02_P5.00mm_691137710002" H 14500 9300 50  0001 C CNN
F 3 "~" H 14500 9300 50  0001 C CNN
	1    14500 9300
	1    0    0    -1  
$EndComp
Wire Wire Line
	14250 9250 14250 9300
Wire Wire Line
	14250 9300 14300 9300
Text GLabel 14300 9400 0    50   Input ~ 0
T5_OUT
Text GLabel 2350 3250 2    50   Input ~ 0
T0_SIGNAL
Text GLabel 2350 3950 2    50   Input ~ 0
T1_SIGNAL
Text GLabel 2350 4450 2    50   Input ~ 0
BUTTONS
$Comp
L Connector_Generic:Conn_01x02 J1
U 1 1 5FE37337
P 850 9450
F 0 "J1" H 768 9125 50  0000 C CNN
F 1 "Conn_01x02" H 768 9216 50  0000 C CNN
F 2 "homebrew:Wurth_ScrewTerminal_1x02_P5.00mm_691137710002" H 850 9450 50  0001 C CNN
F 3 "~" H 850 9450 50  0001 C CNN
	1    850  9450
	-1   0    0    1   
$EndComp
Text GLabel 1050 9350 2    50   Input ~ 0
GPIO_0
$Comp
L power:GND #PWR03
U 1 1 5FE38C07
P 1100 9500
F 0 "#PWR03" H 1100 9250 50  0001 C CNN
F 1 "GND" H 1105 9327 50  0000 C CNN
F 2 "" H 1100 9500 50  0001 C CNN
F 3 "" H 1100 9500 50  0001 C CNN
	1    1100 9500
	1    0    0    -1  
$EndComp
Wire Wire Line
	1100 9500 1100 9450
Wire Wire Line
	1100 9450 1050 9450
$Comp
L Connector_Generic:Conn_01x02 J4
U 1 1 5FE3B4DE
P 1700 9450
F 0 "J4" H 1618 9125 50  0000 C CNN
F 1 "Conn_01x02" H 1618 9216 50  0000 C CNN
F 2 "homebrew:Wurth_ScrewTerminal_1x02_P5.00mm_691137710002" H 1700 9450 50  0001 C CNN
F 3 "~" H 1700 9450 50  0001 C CNN
	1    1700 9450
	-1   0    0    1   
$EndComp
Text GLabel 1900 9350 2    50   Input ~ 0
GPIO_1
$Comp
L power:GND #PWR09
U 1 1 5FE3B659
P 1950 9500
F 0 "#PWR09" H 1950 9250 50  0001 C CNN
F 1 "GND" H 1955 9327 50  0000 C CNN
F 2 "" H 1950 9500 50  0001 C CNN
F 3 "" H 1950 9500 50  0001 C CNN
	1    1950 9500
	1    0    0    -1  
$EndComp
Wire Wire Line
	1950 9500 1950 9450
Wire Wire Line
	1950 9450 1900 9450
Text GLabel 2350 3550 2    50   Input ~ 0
GPIO_0
Text GLabel 2350 3850 2    50   Input ~ 0
GPIO_1
$Comp
L power:VCC #PWR019
U 1 1 5FE4237A
P 14250 9250
F 0 "#PWR019" H 14250 9100 50  0001 C CNN
F 1 "VCC" H 14265 9423 50  0000 C CNN
F 2 "" H 14250 9250 50  0001 C CNN
F 3 "" H 14250 9250 50  0001 C CNN
	1    14250 9250
	1    0    0    -1  
$EndComp
$Comp
L Connector_Generic:Conn_01x03 J2
U 1 1 5FE5815C
P 850 10300
F 0 "J2" H 768 9975 50  0000 C CNN
F 1 "Conn_01x03" H 768 10066 50  0000 C CNN
F 2 "homebrew:Wurth_ScrewTerminal_1x03_P5.00mm_691137710003" H 850 10300 50  0001 C CNN
F 3 "~" H 850 10300 50  0001 C CNN
	1    850  10300
	-1   0    0    1   
$EndComp
Text GLabel 1050 10200 2    50   Input ~ 0
GPIO_2
$Comp
L power:GND #PWR04
U 1 1 5FE5897F
P 1100 10450
F 0 "#PWR04" H 1100 10200 50  0001 C CNN
F 1 "GND" H 1105 10277 50  0000 C CNN
F 2 "" H 1100 10450 50  0001 C CNN
F 3 "" H 1100 10450 50  0001 C CNN
	1    1100 10450
	1    0    0    -1  
$EndComp
Wire Wire Line
	1100 10450 1100 10400
Wire Wire Line
	1100 10400 1050 10400
$Comp
L power:+5V #PWR06
U 1 1 5FE5BA63
P 1550 10300
F 0 "#PWR06" H 1550 10150 50  0001 C CNN
F 1 "+5V" H 1565 10473 50  0000 C CNN
F 2 "" H 1550 10300 50  0001 C CNN
F 3 "" H 1550 10300 50  0001 C CNN
	1    1550 10300
	1    0    0    -1  
$EndComp
Wire Wire Line
	1550 10300 1050 10300
$Comp
L Connector_Generic:Conn_01x03 J5
U 1 1 5FE5F548
P 2100 10300
F 0 "J5" H 2018 9975 50  0000 C CNN
F 1 "Conn_01x03" H 2018 10066 50  0000 C CNN
F 2 "homebrew:Wurth_ScrewTerminal_1x03_P5.00mm_691137710003" H 2100 10300 50  0001 C CNN
F 3 "~" H 2100 10300 50  0001 C CNN
	1    2100 10300
	-1   0    0    1   
$EndComp
$Comp
L power:GND #PWR010
U 1 1 5FE5F75D
P 2350 10450
F 0 "#PWR010" H 2350 10200 50  0001 C CNN
F 1 "GND" H 2355 10277 50  0000 C CNN
F 2 "" H 2350 10450 50  0001 C CNN
F 3 "" H 2350 10450 50  0001 C CNN
	1    2350 10450
	1    0    0    -1  
$EndComp
Wire Wire Line
	2350 10450 2350 10400
Wire Wire Line
	2350 10400 2300 10400
$Comp
L power:+5V #PWR011
U 1 1 5FE5F769
P 2800 10300
F 0 "#PWR011" H 2800 10150 50  0001 C CNN
F 1 "+5V" H 2815 10473 50  0000 C CNN
F 2 "" H 2800 10300 50  0001 C CNN
F 3 "" H 2800 10300 50  0001 C CNN
	1    2800 10300
	1    0    0    -1  
$EndComp
Wire Wire Line
	2800 10300 2300 10300
Text GLabel 2300 10200 2    50   Input ~ 0
GPIO_3
Text GLabel 2350 4650 2    50   Input ~ 0
GPIO_2
Text GLabel 2350 4250 2    50   Input ~ 0
GPIO_3
$Comp
L Connector_Generic:Conn_01x02 J6
U 1 1 5FE756F1
P 3400 9500
F 0 "J6" H 3318 9175 50  0000 C CNN
F 1 "Conn_01x02" H 3318 9266 50  0000 C CNN
F 2 "homebrew:Wurth_ScrewTerminal_1x02_P5.00mm_691137710002" H 3400 9500 50  0001 C CNN
F 3 "~" H 3400 9500 50  0001 C CNN
	1    3400 9500
	-1   0    0    1   
$EndComp
$Comp
L power:+3V3 #PWR013
U 1 1 5FE7641E
P 3800 9350
F 0 "#PWR013" H 3800 9200 50  0001 C CNN
F 1 "+3V3" H 3815 9523 50  0000 C CNN
F 2 "" H 3800 9350 50  0001 C CNN
F 3 "" H 3800 9350 50  0001 C CNN
	1    3800 9350
	1    0    0    -1  
$EndComp
Wire Wire Line
	3800 9350 3800 9400
Wire Wire Line
	3800 9400 3600 9400
$Comp
L Device:R R1
U 1 1 5FE7DC5F
P 3650 9700
F 0 "R1" H 3720 9746 50  0000 L CNN
F 1 "22k" H 3720 9655 50  0000 L CNN
F 2 "Resistor_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P7.62mm_Horizontal" V 3580 9700 50  0001 C CNN
F 3 "~" H 3650 9700 50  0001 C CNN
	1    3650 9700
	1    0    0    -1  
$EndComp
$Comp
L power:GND #PWR012
U 1 1 5FE7E148
P 3650 9850
F 0 "#PWR012" H 3650 9600 50  0001 C CNN
F 1 "GND" H 3655 9677 50  0000 C CNN
F 2 "" H 3650 9850 50  0001 C CNN
F 3 "" H 3650 9850 50  0001 C CNN
	1    3650 9850
	1    0    0    -1  
$EndComp
Wire Wire Line
	3600 9500 3650 9500
Wire Wire Line
	3650 9500 3650 9550
Text GLabel 3700 9500 2    50   Input ~ 0
FSR
Wire Wire Line
	3700 9500 3650 9500
Connection ~ 3650 9500
Text GLabel 2350 4350 2    50   Input ~ 0
FSR
$Comp
L Driver_Motor:Pololu_Breakout_DRV8825 A2
U 1 1 5FE922CE
P 14450 4300
F 0 "A2" H 14200 3550 50  0000 C CNN
F 1 "Pololu_Breakout_DRV8825" H 15100 3550 50  0000 C CNN
F 2 "Module:Pololu_Breakout-16_15.2x20.3mm" H 14650 3500 50  0001 L CNN
F 3 "https://www.pololu.com/product/2982" H 14550 4000 50  0001 C CNN
	1    14450 4300
	1    0    0    -1  
$EndComp
$Comp
L power:+5V #PWR030
U 1 1 5FE922D4
P 14000 3850
F 0 "#PWR030" H 14000 3700 50  0001 C CNN
F 1 "+5V" H 14015 4023 50  0000 C CNN
F 2 "" H 14000 3850 50  0001 C CNN
F 3 "" H 14000 3850 50  0001 C CNN
	1    14000 3850
	1    0    0    -1  
$EndComp
Wire Wire Line
	14000 3850 14000 3900
Wire Wire Line
	14000 3900 14050 3900
$Comp
L power:VCC #PWR032
U 1 1 5FE922DC
P 14450 3250
F 0 "#PWR032" H 14450 3100 50  0001 C CNN
F 1 "VCC" H 14465 3423 50  0000 C CNN
F 2 "" H 14450 3250 50  0001 C CNN
F 3 "" H 14450 3250 50  0001 C CNN
	1    14450 3250
	1    0    0    -1  
$EndComp
$Comp
L Device:CP C3
U 1 1 5FE922E2
P 14900 3500
F 0 "C3" H 15018 3546 50  0000 L CNN
F 1 "100uF" H 15018 3455 50  0000 L CNN
F 2 "Capacitor_THT:CP_Radial_D8.0mm_P3.50mm" H 14938 3350 50  0001 C CNN
F 3 "~" H 14900 3500 50  0001 C CNN
	1    14900 3500
	1    0    0    -1  
$EndComp
Wire Wire Line
	14450 3250 14450 3350
$Comp
L power:GND #PWR036
U 1 1 5FE922E9
P 14900 3650
F 0 "#PWR036" H 14900 3400 50  0001 C CNN
F 1 "GND" H 14905 3477 50  0000 C CNN
F 2 "" H 14900 3650 50  0001 C CNN
F 3 "" H 14900 3650 50  0001 C CNN
	1    14900 3650
	1    0    0    -1  
$EndComp
Wire Wire Line
	14900 3350 14450 3350
Connection ~ 14450 3350
Wire Wire Line
	14450 3350 14450 3700
$Comp
L power:GND #PWR033
U 1 1 5FE922F2
P 14450 5150
F 0 "#PWR033" H 14450 4900 50  0001 C CNN
F 1 "GND" H 14455 4977 50  0000 C CNN
F 2 "" H 14450 5150 50  0001 C CNN
F 3 "" H 14450 5150 50  0001 C CNN
	1    14450 5150
	1    0    0    -1  
$EndComp
Wire Wire Line
	14450 5150 14450 5100
Wire Wire Line
	14550 5150 14550 5100
Wire Wire Line
	14000 3900 14000 4000
Wire Wire Line
	14000 4000 14050 4000
Connection ~ 14000 3900
Wire Wire Line
	14000 4000 14000 4100
Wire Wire Line
	14000 4100 14050 4100
Connection ~ 14000 4000
Text GLabel 14050 4400 0    50   Input ~ 0
STEP2
Text GLabel 14050 4500 0    50   Input ~ 0
DIR2
NoConn ~ 14050 4300
$Comp
L Connector_Generic:Conn_01x04 J12
U 1 1 5FE92309
P 15250 4400
F 0 "J12" H 15330 4392 50  0000 L CNN
F 1 "Conn_01x04" H 15330 4301 50  0000 L CNN
F 2 "Connector_PinHeader_2.54mm:PinHeader_1x04_P2.54mm_Vertical" H 15250 4400 50  0001 C CNN
F 3 "~" H 15250 4400 50  0001 C CNN
	1    15250 4400
	1    0    0    -1  
$EndComp
Wire Wire Line
	14850 4200 15050 4200
Wire Wire Line
	15050 4200 15050 4300
Wire Wire Line
	14850 4300 14950 4300
Wire Wire Line
	14950 4300 14950 4400
Wire Wire Line
	14950 4400 15050 4400
Wire Wire Line
	14850 4500 15050 4500
Wire Wire Line
	14850 4600 15050 4600
$Comp
L Switch:SW_DIP_x03 SW2
U 1 1 5FE92316
P 13400 4900
F 0 "SW2" H 13400 5367 50  0000 C CNN
F 1 "SW_DIP_x03" H 13400 5276 50  0000 C CNN
F 2 "Button_Switch_THT:SW_DIP_SPSTx03_Slide_9.78x9.8mm_W7.62mm_P2.54mm" H 13400 4900 50  0001 C CNN
F 3 "~" H 13400 4900 50  0001 C CNN
	1    13400 4900
	1    0    0    -1  
$EndComp
$Comp
L power:+5V #PWR028
U 1 1 5FE9231C
P 13000 4650
F 0 "#PWR028" H 13000 4500 50  0001 C CNN
F 1 "+5V" H 13015 4823 50  0000 C CNN
F 2 "" H 13000 4650 50  0001 C CNN
F 3 "" H 13000 4650 50  0001 C CNN
	1    13000 4650
	1    0    0    -1  
$EndComp
Wire Wire Line
	13000 4650 13000 4700
Wire Wire Line
	13000 4700 13100 4700
Wire Wire Line
	13000 4700 13000 4800
Wire Wire Line
	13000 4800 13100 4800
Connection ~ 13000 4700
Wire Wire Line
	13000 4800 13000 4900
Wire Wire Line
	13000 4900 13100 4900
Connection ~ 13000 4800
Wire Wire Line
	13700 4900 14050 4900
Wire Wire Line
	13700 4800 14050 4800
Wire Wire Line
	13700 4700 14050 4700
Wire Wire Line
	11200 2550 11300 2550
Connection ~ 11200 2550
Wire Wire Line
	14500 2550 14600 2550
Connection ~ 14500 2550
Wire Wire Line
	14550 5150 14450 5150
Connection ~ 14450 5150
Text GLabel 2350 4150 2    50   Input ~ 0
STEP2
Text GLabel 2350 4550 2    50   Input ~ 0
DIR2
NoConn ~ 2350 2750
NoConn ~ 2350 2850
NoConn ~ 2350 2950
NoConn ~ 1350 1600
$Comp
L power:PWR_FLAG #FLG02
U 1 1 5FF1DB70
P 1600 2450
F 0 "#FLG02" H 1600 2525 50  0001 C CNN
F 1 "PWR_FLAG" H 1600 2623 50  0000 C CNN
F 2 "" H 1600 2450 50  0001 C CNN
F 3 "~" H 1600 2450 50  0001 C CNN
	1    1600 2450
	1    0    0    -1  
$EndComp
Wire Wire Line
	1600 2450 1250 2450
Connection ~ 1250 2450
$Comp
L power:PWR_FLAG #FLG01
U 1 1 5FF356E6
P 700 2800
F 0 "#FLG01" H 700 2875 50  0001 C CNN
F 1 "PWR_FLAG" H 700 2973 50  0000 C CNN
F 2 "" H 700 2800 50  0001 C CNN
F 3 "~" H 700 2800 50  0001 C CNN
	1    700  2800
	1    0    0    -1  
$EndComp
Wire Wire Line
	700  2800 700  2850
Wire Wire Line
	700  2850 1050 2850
Connection ~ 1050 2850
NoConn ~ 12600 8350
NoConn ~ 13400 8350
$Comp
L Switch:SW_SPST SW4
U 1 1 5FF58463
P 5450 1000
F 0 "SW4" H 5450 1235 50  0000 C CNN
F 1 "SW_SPST" H 5450 1144 50  0000 C CNN
F 2 "snapeda:SW_1825910-6-4" H 5450 1000 50  0001 C CNN
F 3 "~" H 5450 1000 50  0001 C CNN
	1    5450 1000
	1    0    0    -1  
$EndComp
$Comp
L Device:R R2
U 1 1 5FF5D53A
P 5050 1000
F 0 "R2" V 4843 1000 50  0000 C CNN
F 1 "10k" V 4934 1000 50  0000 C CNN
F 2 "Resistor_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P7.62mm_Horizontal" V 4980 1000 50  0001 C CNN
F 3 "~" H 5050 1000 50  0001 C CNN
	1    5050 1000
	0    1    1    0   
$EndComp
Wire Wire Line
	5200 1000 5250 1000
$Comp
L Switch:SW_SPST SW5
U 1 1 5FF6455E
P 5450 1450
F 0 "SW5" H 5450 1685 50  0000 C CNN
F 1 "SW_SPST" H 5450 1594 50  0000 C CNN
F 2 "snapeda:SW_1825910-6-4" H 5450 1450 50  0001 C CNN
F 3 "~" H 5450 1450 50  0001 C CNN
	1    5450 1450
	1    0    0    -1  
$EndComp
$Comp
L Device:R R3
U 1 1 5FF64840
P 5050 1450
F 0 "R3" V 4843 1450 50  0000 C CNN
F 1 "22k" V 4934 1450 50  0000 C CNN
F 2 "Resistor_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P7.62mm_Horizontal" V 4980 1450 50  0001 C CNN
F 3 "~" H 5050 1450 50  0001 C CNN
	1    5050 1450
	0    1    1    0   
$EndComp
Wire Wire Line
	5200 1450 5250 1450
$Comp
L Switch:SW_SPST SW6
U 1 1 5FF6C98D
P 5450 1900
F 0 "SW6" H 5450 2135 50  0000 C CNN
F 1 "SW_SPST" H 5450 2044 50  0000 C CNN
F 2 "snapeda:SW_1825910-6-4" H 5450 1900 50  0001 C CNN
F 3 "~" H 5450 1900 50  0001 C CNN
	1    5450 1900
	1    0    0    -1  
$EndComp
$Comp
L Device:R R4
U 1 1 5FF6CC93
P 5050 1900
F 0 "R4" V 4843 1900 50  0000 C CNN
F 1 "47k" V 4934 1900 50  0000 C CNN
F 2 "Resistor_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P7.62mm_Horizontal" V 4980 1900 50  0001 C CNN
F 3 "~" H 5050 1900 50  0001 C CNN
	1    5050 1900
	0    1    1    0   
$EndComp
Wire Wire Line
	5200 1900 5250 1900
$Comp
L Switch:SW_SPST SW7
U 1 1 5FF6CC9E
P 5450 2350
F 0 "SW7" H 5450 2585 50  0000 C CNN
F 1 "SW_SPST" H 5450 2494 50  0000 C CNN
F 2 "snapeda:SW_1825910-6-4" H 5450 2350 50  0001 C CNN
F 3 "~" H 5450 2350 50  0001 C CNN
	1    5450 2350
	1    0    0    -1  
$EndComp
$Comp
L Device:R R5
U 1 1 5FF6CCA8
P 5050 2350
F 0 "R5" V 4843 2350 50  0000 C CNN
F 1 "68k" V 4934 2350 50  0000 C CNN
F 2 "Resistor_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P7.62mm_Horizontal" V 4980 2350 50  0001 C CNN
F 3 "~" H 5050 2350 50  0001 C CNN
	1    5050 2350
	0    1    1    0   
$EndComp
Wire Wire Line
	5200 2350 5250 2350
$Comp
L power:+3V3 #PWR038
U 1 1 5FFEDF9C
P 4650 850
F 0 "#PWR038" H 4650 700 50  0001 C CNN
F 1 "+3V3" H 4665 1023 50  0000 C CNN
F 2 "" H 4650 850 50  0001 C CNN
F 3 "" H 4650 850 50  0001 C CNN
	1    4650 850 
	1    0    0    -1  
$EndComp
Wire Wire Line
	4650 850  4900 850 
Wire Wire Line
	4900 850  4900 1000
Wire Wire Line
	4900 1000 4900 1450
Connection ~ 4900 1000
Wire Wire Line
	4900 1450 4900 1900
Connection ~ 4900 1450
Wire Wire Line
	4900 1900 4900 2350
Connection ~ 4900 1900
Wire Wire Line
	5650 1000 5700 1000
Wire Wire Line
	5700 1000 5700 1450
Wire Wire Line
	5700 2350 5650 2350
Wire Wire Line
	5650 1900 5700 1900
Connection ~ 5700 1900
Wire Wire Line
	5650 1450 5700 1450
Connection ~ 5700 1450
Wire Wire Line
	5700 1450 5700 1900
$Comp
L Device:R R6
U 1 1 60018FBB
P 5700 2600
F 0 "R6" H 5770 2646 50  0000 L CNN
F 1 "100k" H 5770 2555 50  0000 L CNN
F 2 "Resistor_THT:R_Axial_DIN0207_L6.3mm_D2.5mm_P7.62mm_Horizontal" V 5630 2600 50  0001 C CNN
F 3 "~" H 5700 2600 50  0001 C CNN
	1    5700 2600
	1    0    0    -1  
$EndComp
Wire Wire Line
	5700 2450 5700 2350
Connection ~ 5700 2350
$Comp
L power:GND #PWR039
U 1 1 60020DB0
P 5700 2750
F 0 "#PWR039" H 5700 2500 50  0001 C CNN
F 1 "GND" H 5705 2577 50  0000 C CNN
F 2 "" H 5700 2750 50  0001 C CNN
F 3 "" H 5700 2750 50  0001 C CNN
	1    5700 2750
	1    0    0    -1  
$EndComp
Text GLabel 6600 2450 2    50   Input ~ 0
BUTTONS
Text Notes 11800 3300 0    50   ~ 0
DRV8825/A4988\nStepper driver modules
Text Notes 11800 7000 0    50   ~ 0
Unipolar (5-wire) stepper driver\nand aux transistor outputs
Text Notes 850  8750 0    50   ~ 0
GPIO
Text Notes 6100 950  0    50   ~ 0
Pushbuttons
$Comp
L Device:C C5
U 1 1 60043570
P 6150 2600
F 0 "C5" H 6265 2646 50  0000 L CNN
F 1 "0.1uF" H 6265 2555 50  0000 L CNN
F 2 "Capacitor_THT:C_Disc_D5.1mm_W3.2mm_P5.00mm" H 6188 2450 50  0001 C CNN
F 3 "~" H 6150 2600 50  0001 C CNN
	1    6150 2600
	1    0    0    -1  
$EndComp
Wire Wire Line
	6150 2450 5700 2450
Connection ~ 5700 2450
Wire Wire Line
	5700 2750 6150 2750
Connection ~ 5700 2750
Wire Wire Line
	6600 2450 6150 2450
Connection ~ 6150 2450
Wire Wire Line
	5700 1900 5700 2350
$Comp
L power:GND #PWR024
U 1 1 5FDCAA90
P 5300 6350
F 0 "#PWR024" H 5300 6100 50  0001 C CNN
F 1 "GND" H 5305 6177 50  0000 C CNN
F 2 "" H 5300 6350 50  0001 C CNN
F 3 "" H 5300 6350 50  0001 C CNN
	1    5300 6350
	1    0    0    -1  
$EndComp
$Comp
L power:+3V3 #PWR027
U 1 1 5FDD2807
P 8600 6550
F 0 "#PWR027" H 8600 6400 50  0001 C CNN
F 1 "+3V3" H 8615 6723 50  0000 C CNN
F 2 "" H 8600 6550 50  0001 C CNN
F 3 "" H 8600 6550 50  0001 C CNN
	1    8600 6550
	1    0    0    -1  
$EndComp
Text GLabel 7550 6450 2    50   Input ~ 0
SD_CLK
Text GLabel 7550 6650 2    50   Input ~ 0
SD_MOSI
Text GLabel 7550 6250 2    50   Input ~ 0
SD_MISO
Text GLabel 5750 6550 0    50   Input ~ 0
SD_CS
$Comp
L snapeda-1051620001:105162-0001 J10
U 1 1 5FDDDB50
P 5750 6050
F 0 "J10" H 6650 6437 60  0000 C CNN
F 1 "105162-0001" H 6650 6331 60  0000 C CNN
F 2 "snapeda:105162-0001" H 6650 6290 60  0001 C CNN
F 3 "" H 5750 6050 60  0000 C CNN
	1    5750 6050
	1    0    0    -1  
$EndComp
Wire Wire Line
	5300 6350 5750 6350
Wire Wire Line
	5750 6350 5750 6250
Connection ~ 5750 6350
Wire Wire Line
	5750 6250 5750 6150
Connection ~ 5750 6250
Wire Wire Line
	5750 6150 5750 6050
Connection ~ 5750 6150
$Comp
L power:GND #PWR026
U 1 1 5FE0179B
P 8250 6300
F 0 "#PWR026" H 8250 6050 50  0001 C CNN
F 1 "GND" H 8255 6127 50  0000 C CNN
F 2 "" H 8250 6300 50  0001 C CNN
F 3 "" H 8250 6300 50  0001 C CNN
	1    8250 6300
	1    0    0    -1  
$EndComp
Wire Wire Line
	7550 6550 8600 6550
Wire Wire Line
	8250 6300 8100 6300
Wire Wire Line
	8100 6300 8100 6350
Wire Wire Line
	8100 6350 7550 6350
NoConn ~ 7550 6150
NoConn ~ 5750 6450
Text GLabel 2350 3350 2    50   Input ~ 0
SD_MISO
Text GLabel 2350 4050 2    50   Input ~ 0
SD_MOSI
Text GLabel 2350 4950 2    50   Input ~ 0
SD_CLK
Text GLabel 2350 5150 2    50   Input ~ 0
SD_CS
NoConn ~ 7550 6050
$Comp
L Device:C C6
U 1 1 5FE4A609
P 8700 6750
F 0 "C6" H 8815 6796 50  0000 L CNN
F 1 "0.1uF" H 8815 6705 50  0000 L CNN
F 2 "Capacitor_THT:C_Disc_D5.1mm_W3.2mm_P5.00mm" H 8738 6600 50  0001 C CNN
F 3 "~" H 8700 6750 50  0001 C CNN
	1    8700 6750
	1    0    0    -1  
$EndComp
$Comp
L Device:CP C7
U 1 1 5FE4B3F3
P 9200 6750
F 0 "C7" H 9318 6796 50  0000 L CNN
F 1 "100uF" H 9318 6705 50  0000 L CNN
F 2 "Capacitor_THT:CP_Radial_D8.0mm_P3.50mm" H 9238 6600 50  0001 C CNN
F 3 "~" H 9200 6750 50  0001 C CNN
	1    9200 6750
	1    0    0    -1  
$EndComp
Wire Wire Line
	8600 6550 8700 6550
Wire Wire Line
	9200 6550 9200 6600
Connection ~ 8600 6550
Wire Wire Line
	8700 6600 8700 6550
Connection ~ 8700 6550
Wire Wire Line
	8700 6550 9200 6550
$Comp
L power:GND #PWR040
U 1 1 5FE5D28A
P 8700 6900
F 0 "#PWR040" H 8700 6650 50  0001 C CNN
F 1 "GND" H 8705 6727 50  0000 C CNN
F 2 "" H 8700 6900 50  0001 C CNN
F 3 "" H 8700 6900 50  0001 C CNN
	1    8700 6900
	1    0    0    -1  
$EndComp
$Comp
L power:GND #PWR041
U 1 1 5FE5D577
P 9200 6900
F 0 "#PWR041" H 9200 6650 50  0001 C CNN
F 1 "GND" H 9205 6727 50  0000 C CNN
F 2 "" H 9200 6900 50  0001 C CNN
F 3 "" H 9200 6900 50  0001 C CNN
	1    9200 6900
	1    0    0    -1  
$EndComp
Text Notes 5600 5750 0    50   ~ 0
microSD card
$Comp
L Device:D_TVS D1
U 1 1 5FDFB6CB
P 2450 1050
F 0 "D1" V 2404 1130 50  0000 L CNN
F 1 "D_TVS" V 2495 1130 50  0000 L CNN
F 2 "Diode_THT:D_DO-15_P10.16mm_Horizontal" H 2450 1050 50  0001 C CNN
F 3 "~" H 2450 1050 50  0001 C CNN
	1    2450 1050
	0    1    1    0   
$EndComp
Wire Wire Line
	2050 1000 2300 1000
Wire Wire Line
	2300 1000 2300 900 
Wire Wire Line
	2300 900  2450 900 
Connection ~ 2050 1000
Wire Wire Line
	2050 1100 2300 1100
Wire Wire Line
	2300 1100 2300 1200
Wire Wire Line
	2300 1200 2450 1200
Connection ~ 2050 1100
$Comp
L Connector_Generic:Conn_01x03 J14
U 1 1 5FE199FD
P 900 5850
F 0 "J14" H 900 5650 50  0000 C CNN
F 1 "Conn_01x03" V 1000 5800 50  0000 C CNN
F 2 "homebrew:Wurth_ScrewTerminal_1x03_P5.00mm_691137710003" H 900 5850 50  0001 C CNN
F 3 "~" H 900 5850 50  0001 C CNN
	1    900  5850
	-1   0    0    1   
$EndComp
$Comp
L power:GND #PWR043
U 1 1 5FE1A857
P 1100 5950
F 0 "#PWR043" H 1100 5700 50  0001 C CNN
F 1 "GND" H 1105 5777 50  0000 C CNN
F 2 "" H 1100 5950 50  0001 C CNN
F 3 "" H 1100 5950 50  0001 C CNN
	1    1100 5950
	1    0    0    -1  
$EndComp
$Comp
L power:+5V #PWR042
U 1 1 5FE1B265
P 1100 5750
F 0 "#PWR042" H 1100 5600 50  0001 C CNN
F 1 "+5V" H 1115 5923 50  0000 C CNN
F 2 "" H 1100 5750 50  0001 C CNN
F 3 "" H 1100 5750 50  0001 C CNN
	1    1100 5750
	-1   0    0    -1  
$EndComp
$Comp
L power:+3V3 #PWR044
U 1 1 5FE1B737
P 1350 5850
F 0 "#PWR044" H 1350 5700 50  0001 C CNN
F 1 "+3V3" H 1365 6023 50  0000 C CNN
F 2 "" H 1350 5850 50  0001 C CNN
F 3 "" H 1350 5850 50  0001 C CNN
	1    1350 5850
	1    0    0    -1  
$EndComp
Wire Wire Line
	1350 5850 1100 5850
$Comp
L Mechanical:MountingHole H1
U 1 1 5FE7D663
P 6300 8600
F 0 "H1" H 6400 8646 50  0000 L CNN
F 1 "MountingHole" H 6400 8555 50  0000 L CNN
F 2 "MountingHole:MountingHole_3.2mm_M3" H 6300 8600 50  0001 C CNN
F 3 "~" H 6300 8600 50  0001 C CNN
	1    6300 8600
	1    0    0    -1  
$EndComp
$Comp
L Mechanical:MountingHole H2
U 1 1 5FE7E3EF
P 6300 8800
F 0 "H2" H 6400 8846 50  0000 L CNN
F 1 "MountingHole" H 6400 8755 50  0000 L CNN
F 2 "MountingHole:MountingHole_3.2mm_M3" H 6300 8800 50  0001 C CNN
F 3 "~" H 6300 8800 50  0001 C CNN
	1    6300 8800
	1    0    0    -1  
$EndComp
$Comp
L Mechanical:MountingHole H3
U 1 1 5FE7ECD1
P 6300 9000
F 0 "H3" H 6400 9046 50  0000 L CNN
F 1 "MountingHole" H 6400 8955 50  0000 L CNN
F 2 "MountingHole:MountingHole_3.2mm_M3" H 6300 9000 50  0001 C CNN
F 3 "~" H 6300 9000 50  0001 C CNN
	1    6300 9000
	1    0    0    -1  
$EndComp
Text GLabel 2350 3050 2    50   Input ~ 0
T3_SIGNAL
Text GLabel 2350 3450 2    50   Input ~ 0
T4_SIGNAL
Text GLabel 2350 3150 2    50   Input ~ 0
T5_SIGNAL
Text GLabel 2350 4850 2    50   Input ~ 0
T2_SIGNAL
$Comp
L Mechanical:MountingHole H4
U 1 1 5FE7F202
P 6300 9200
F 0 "H4" H 6400 9246 50  0000 L CNN
F 1 "MountingHole" H 6400 9155 50  0000 L CNN
F 2 "MountingHole:MountingHole_3.2mm_M3" H 6300 9200 50  0001 C CNN
F 3 "~" H 6300 9200 50  0001 C CNN
	1    6300 9200
	1    0    0    -1  
$EndComp
$EndSCHEMATC