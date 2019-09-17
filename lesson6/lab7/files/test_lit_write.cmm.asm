	.text		#text segment
	.globl  main		#main function
main:	
	sw      $ra, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $fp, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	subu    $sp, $sp, 8
	addu    $fp, $sp, 16
	subu    $t0, $fp, 12		#load address of local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $v0, 5
	syscall
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	sw      $v0, 0($t0)	#Store value to address of lhs
	subu    $t0, $fp, 8		#load address of local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $v0, 5
	syscall
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	sw      $v0, 0($t0)	#Store value to address of lhs
	lw      $t0, -12($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $v0, 1
	lw      $a0, 4($sp)	#POP
	addu    $sp, $sp, 4
	syscall
	lw      $t0, -8($fp)	#load local variable
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
