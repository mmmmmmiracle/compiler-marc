	.text		#text segment
_max:	
	sw      $ra, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $fp, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	subu    $sp, $sp, 0
	addu    $fp, $sp, 8
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
	sgt     $t0, $t1, $t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	beqz    $t0, ELSE_2
	lw      $t0, 4($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $v0, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $ra, 0($fp)
	move    $t0, $fp
	lw      $fp, -4($fp)
	move    $sp, $t0
	jr      $ra
	j       ENDIF_2
ELSE_2:	
	lw      $t0, 8($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $v0, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $ra, 0($fp)
	move    $t0, $fp
	lw      $fp, -4($fp)
	move    $sp, $t0
	jr      $ra
ENDIF_2:	
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
	subu    $sp, $sp, 12
	addu    $fp, $sp, 20
	.data		#data segment
STR_13:	.asciiz "a: "	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_13
	syscall
	subu    $t0, $fp, 8		#load address of local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $v0, 5
	syscall
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	sw      $v0, 0($t0)	#Store value to address of lhs
	.data		#data segment
STR_14:	.asciiz "b: "	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_14
	syscall
	subu    $t0, $fp, 12		#load address of local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $v0, 5
	syscall
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	sw      $v0, 0($t0)	#Store value to address of lhs
	subu    $t0, $fp, 16		#load address of local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, -12($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, -8($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	jal     _max
	addu    $sp, $sp, 8
	sw      $v0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	sw      $t1, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $t1, 0($t0)	#Store value to address of lhs
	addu    $sp, $sp, 4		#Pop the result of the assignment
	.data		#data segment
STR_15:	.asciiz "the maximum is "	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_15
	syscall
	lw      $t0, -16($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $v0, 1
	lw      $a0, 4($sp)	#POP
	addu    $sp, $sp, 4
	syscall
	lw      $ra, 0($fp)
	move    $t0, $fp
	lw      $fp, -4($fp)
	move    $sp, $t0
	li      $v0, 10
	syscall
