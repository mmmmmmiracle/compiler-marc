	.text		#text segment
_add:	
	sw      $ra, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $fp, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	addu    $fp, $sp, 8
	subu    $sp, $sp, 0
	lw      $t0, 4($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $a0, 4($sp)	#POP
	addu    $sp, $sp, 4
	li      $v0, 1
	syscall
	.data		#data segment
STR_8:	.asciiz " + "	#string value
	.text		#back to text segment
	la      $a0, STR_8
	li      $v0, 4
	syscall
	lw      $t0, 8($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $a0, 4($sp)	#POP
	addu    $sp, $sp, 4
	li      $v0, 1
	syscall
	.data		#data segment
STR_9:	.asciiz " = "	#string value
	.text		#back to text segment
	la      $a0, STR_9
	li      $v0, 4
	syscall
	lw      $t0, 4($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 8($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $t1, 4($sp)	#POP
	addu    $sp, $sp, 4
	add     $t0, $t1, $t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $a0, 4($sp)	#POP
	addu    $sp, $sp, 4
	li      $v0, 1
	syscall
	.data		#data segment
STR_10:	.asciiz "\n"	#string value
	.text		#back to text segment
	la      $a0, STR_10
	li      $v0, 4
	syscall
	lw      $ra, 0($fp)
	move    $t0, $fp
	lw      $fp, -4($fp)
	move    $sp, $t0
	jr      $ra
	.text		#text segment
	.globl  main		#main function
main:	
	sw      $ra, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $fp, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	addu    $fp, $sp, 8
	subu    $sp, $sp, 8
	.data		#data segment
STR_11:	.asciiz "Please input adder1:   "	#string value
	.text		#back to text segment
	la      $a0, STR_11
	li      $v0, 4
	syscall
	subu    $t0, $fp, 8		#load address of local variable
	li      $v0, 5
	syscall
	sw      $v0, 0($t0)	#Store value to address of lhs
	.data		#data segment
STR_12:	.asciiz "Please input adder2:   "	#string value
	.text		#back to text segment
	la      $a0, STR_12
	li      $v0, 4
	syscall
	subu    $t0, $fp, 12		#load address of local variable
	li      $v0, 5
	syscall
	sw      $v0, 0($t0)	#Store value to address of lhs
	lw      $t0, -12($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, -8($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	jal     _add
	addu    $sp, $sp, 8
	lw      $ra, 0($fp)
	move    $t0, $fp
	lw      $fp, -4($fp)
	move    $sp, $t0
	li      $v0, 10
	syscall
