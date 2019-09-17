	.data		#data segment
	.align  2
_var_bool:	.word   0	#global variable
	.data		#data segment
	.align  2
_var_int:	.word   0	#global variable
	.text		#text segment
_func1:	
	sw      $ra, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $fp, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	subu    $sp, $sp, 0
	addu    $fp, $sp, 8
	lw      $ra, 0($fp)
	move    $t0, $fp
	lw      $fp, -4($fp)
	move    $sp, $t0
	jr      $ra
	.text		#text segment
_func2:	
	sw      $ra, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $fp, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	subu    $sp, $sp, 0
	addu    $fp, $sp, 8
	li      $t0, 0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $v0, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $ra, 0($fp)
	move    $t0, $fp
	lw      $fp, -4($fp)
	move    $sp, $t0
	jr      $ra
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
	subu    $sp, $sp, 0
	addu    $fp, $sp, 8
	lw      $ra, 0($fp)
	move    $t0, $fp
	lw      $fp, -4($fp)
	move    $sp, $t0
	li      $v0, 10
	syscall
