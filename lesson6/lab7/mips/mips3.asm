.data
	x:	.asciiz		"\nEnter the maximum value: "
	y:	.asciiz		"\nThe sum is: "
.text
.globl	main

main:
	la $a0, x	# Loads the address of x into the argument register.
	li $v0, 4	# System call code to print a string.
	syscall		# Print x.
	
	li $v0, 5 	# System call code that reads the integer.
	syscall		# Reads the integer that the user had inputted.
	
	move $t0, $v0	   # maximum
	addi $t1, $zero, 1 # counter
	addi $t2, $zero, 0 # sum
loop:
	add $t2, $t2, $t1 # t2 = t2 + t1
	beq $t1, $t0, end
	addi $t1, $t1, 1
	j loop
end:
	la $a0, y	# Loads the address of z into the argument register.
	li $v0, 4	# System call code to print a string.
	syscall		# Print z.
	
	move $a0, $t2	# Moves the sum in $t1 into the argument register.
	li $v0, 1	# System call code to print an integer.
	syscall		# Prints the sum.
	
	li $v0, 10	# System call code to exit the program.
	syscall		# Exit the program.		
	
