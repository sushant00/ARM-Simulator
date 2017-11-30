mov r1, #10
sub sp,sp,#12
str lr,[sp,#0]
str r1,[sp,#4]
sub r1,r1,#1
str r1,[sp,#8]
swi 0x11