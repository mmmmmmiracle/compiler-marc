	.text		#text segment
	.globl  main		#main function
main:	
	sw      $ra, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $fp, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	addu    $fp, $sp, 8
	subu    $sp, $sp, 12
	subu    $t0, $fp, 8		#load the address of local struct variable.
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	subu    $t0, 0		#$t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	subu    $t0, 0		#$t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $t0, 2
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	sw      $t1, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $t1, 0($t0)	#Store value to address of lhs
	addu    $sp, $sp, 4		#Pop the result of the assignment
	subu    $t0, $fp, 8		#load the address of local struct variable.
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	subu    $t0, 0		#$t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	subu    $t0, 4		#$t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $t0, 51
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	sw      $t1, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $t1, 0($t0)	#Store value to address of lhs
	addu    $sp, $sp, 4		#Pop the result of the assignment
	subu    $t0, $fp, 8		#load the address of local struct variable.
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	subu    $t0, 8		#$t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	subu    $t0, $fp, 8		#load the address of local struct variable.
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	subu    $t0, 0		#$t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	subu    $t0, 0		#$t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	subu    $t0, $fp, 8		#load the address of local struct variable.
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	subu    $t0, 0		#$t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	subu    $t0, 4		#$t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $t0, 0($t0)
	lw      $t1, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $t1, 0($t1)
	add     $t0, $t1, $t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	sw      $t1, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $t1, 0($t0)	#Store value to address of lhs
	addu    $sp, $sp, 4		#Pop the result of the assignment
	subu    $t0, $fp, 8		#load the address of local struct variable.
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	subu    $t0, 0		#$t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	subu    $t0, 0		#$t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $a0, 4($sp)	#POP
	addu    $sp, $sp, 4
	li      $v0, 1
	syscall
	.data		#data segment
STR_1:	.asciiz " + "	#string value
	.text		#back to text segment
	la      $a0, STR_1
	li      $v0, 4
	syscall
	subu    $t0, $fp, 8		#load the address of local struct variable.
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	subu    $t0, 0		#$t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	subu    $t0, 4		#$t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $a0, 4($sp)	#POP
	addu    $sp, $sp, 4
	li      $v0, 1
	syscall
	.data		#data segment
STR_2:	.asciiz " = "	#string value
	.text		#back to text segment
	la      $a0, STR_2
	li      $v0, 4
	syscall
	subu    $t0, $fp, 8		#load the address of local struct variable.
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	subu    $t0, 8		#$t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $a0, 4($sp)	#POP
	addu    $sp, $sp, 4
	li      $v0, 1
	syscall
	.data		#data segment
STR_3:	.asciiz "\n"	#string value
	.text		#back to text segment
	la      $a0, STR_3
	li      $v0, 4
	syscall
	lw      $ra, 0($fp)
	move    $t0, $fp
	lw      $fp, -4($fp)
	move    $sp, $t0
	li      $v0, 10
	syscall
