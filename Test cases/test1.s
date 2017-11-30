mov r4, #10 
mov r5, #20 
mov r6, r4
mov r4, r5
mov r5, r6
mov r0, #1
mov r1, r5
swi 0x6b
swi 0x11