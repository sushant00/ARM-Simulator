# ARM-Simulator
### &nbsp;&nbsp;&#x1F4D8;Language : ARM to JAVA

<br><br>
<h3> &nbsp;&nbsp;&nbsp;&nbsp; Developers</h3>
    

-----------------------------------------------------------------------------------------------------------------------------------

Adarsh Kumar Choudhary  [adarshk007](https://github.com/adarshk007)
<br>
Sushant Kumar Singh        [sushant00](https://github.com/sushant00)

<br>
<h3 align="center"> Description</h3>

--------------------------------------------------------------------------------------------------------------------------------

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
for example:

-	<b>Fetch prints:</b>
    o	“FETCH:Fetch instruction 0xE3A0200A from address 0x0” 
    
-	<b>Decode</b>
    o	“DECODE: Operation is ADD, first operand R2, Second operand R3, destination register R1”
    o	“DECODE:  Read registers R2 = 10, R3 = 2”
    
-	<b>Execute</b>
    o	“EXECUTE: ADD 10 and 2”
    
-	<b>Memory</b>
    o	“MEMORY:No memory  operation”
    
-	<b>Write-back</b>
    o	“WRITEBACK: write 12 to R1”

    
</p></pre>
<br>
<br>

<h4 align="center">ARM-GUI</h4>

--------------------------------------------------------------------------------------------------------------------------------

<p align="center">
<img src="https://user-images.githubusercontent.com/23396919/34083182-01a29c4c-e392-11e7-9291-fbe084ae892c.png" width="450"></img><br><br>
<img src="https://user-images.githubusercontent.com/23396919/34083179-fe96f534-e391-11e7-80f6-99007d6b6aba.png" width="450"></img><br><br>
<img src="https://user-images.githubusercontent.com/23396919/34083176-fb1044c4-e391-11e7-8d2f-77120ef47e44.png" width="450"></img><br><br>
<img src="https://user-images.githubusercontent.com/23396919/34083176-fb1044c4-e391-11e7-8d2f-77120ef47e44.png" width="450"></img><br><br>
</p>
