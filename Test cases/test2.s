mov r4, #0 
mov r5, #0
mov r6, #10
loop: add r4,r4 ,r5
	add r5,r5 ,#1
	cmp r5 ,r6
ble loop
mov r0, #1
mov r1, r4
swi 0x6b
swi 0x11