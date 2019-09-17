.data
	x:	.asciiz		"\nEnter x value: "
	y:	.asciiz		"\nEnter y value: "
	z:	.ascii		"\nThe maximum is: "
.text
.globl	main

main:
	la $a0, x	# Loads the address of x into the argument register.
	li $v0, 4	# System call code to print a string.
	syscall		# Print x.
	
	li $v0, 5 	# System call code that reads the integer.
	syscall		# Reads the integer that the user had inputted.
	
	move $t0, $v0	# Moves content of $v0 into $t0
	
	la $a0, y	# Loads the address of x into the argument register.
	li $v0, 4	# System call code to print a string.
	syscall		# Print y.
	
	li $v0, 5 	# System call code that reads the integer.
	syscall		# Reads the integer that the user had inputted.
	
	move $t1, $v0	# Moves content of $v0 into $t1
	
	bge $t1, $t0, end
	move $t1, $t0

end:
	la $a0, z	# Loads the address of z into the argument register.
	li $v0, 4	# System call code to print a string.
	syscall		# Print z.
	
	move $a0, $t1	# Moves the maximum in $t1 into the argument register.
	li $v0, 1	# System call code to print an integer.
	syscall		# Prints the maximum.
	
	li $v0, 10	# System call code to exit the program.
	syscall		# Exit the program.	
	
