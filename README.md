# ARM-Simulator
### &nbsp;&nbsp;&#x1F4D8;Language : ARM to JAVA

<br><br>
<h3> &nbsp;&nbsp;&nbsp;&nbsp; Developers</h3>
    

-----------------------------------------------------------------------------------------------------------------------------------
<pre>
<p align="left">
<img  src="https://user-images.githubusercontent.com/23396919/34082443-a30c8484-e384-11e7-915f-73f53cb83252.jpeg" width="50"> </img>       <img src="https://user-images.githubusercontent.com/23396919/34082444-a62cc9ee-e384-11e7-81d6-bfd0d7c9ed7d.jpeg" width="50">
</p></pre>
<br>
<h3 align="center"> Description</h3>

-----------------------------------------------------------------------------------------------------------------------------------
<pre><p>
The simulator: 
    - reads the instruction from instruction memory
    - decodes the instruction
    - read the register
    - execute the operation
    - write back to the register file. 

The execution of instruction continues till it reaches instruction “swi 0x11”. 
In other words as soon as instruction reads “0xEF000011”, simulator stops 
and writes the updated memory contents on to a memory text file. 

The simulator also prints messages for each stage
for example for the third instruction above following messages are printed.

-	Fetch prints:
    o	“FETCH:Fetch instruction 0xE3A0200A from address 0x0” 
    
-	Decode
    o	“DECODE: Operation is ADD, first operand R2, Second operand R3, destination register R1”
    o	“DECODE:  Read registers R2 = 10, R3 = 2”
    
-	Execute
    o	“EXECUTE: ADD 10 and 2”
    
-	Memory
    o	“MEMORY:No memory  operation”
    
-	Write-back
    o	“WRITEBACK: write 12 to R1”

    
</p></pre>
