	.text		#text segment
_sayHello:	
	sw      $ra, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $fp, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	addu    $fp, $sp, 8
	subu    $sp, $sp, 0
	.data		#data segment
STR_1:	.asciiz "salut\n"	#string value
	.text		#back to text segment
	la      $a0, STR_1
	li      $v0, 4
	syscall
	.data		#data segment
STR_2:	.asciiz "ni hao\n"	#string value
	.text		#back to text segment
	la      $a0, STR_2
	li      $v0, 4
	syscall
	.data		#data segment
STR_3:	.asciiz "hello\n"	#string value
	.text		#back to text segment
	la      $a0, STR_3
	li      $v0, 4
	syscall
	.data		#data segment
STR_4:	.asciiz "hallo\n"	#string value
	.text		#back to text segment
	la      $a0, STR_4
	li      $v0, 4
	syscall
	.data		#data segment
STR_5:	.asciiz "ola\n"	#string value
	.text		#back to text segment
	la      $a0, STR_5
	li      $v0, 4
	syscall
	.data		#data segment
STR_6:	.asciiz "hola\n"	#string value
	.text		#back to text segment
	la      $a0, STR_6
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
	subu    $sp, $sp, 0
	jal     _sayHello
	addu    $sp, $sp, 0
	lw      $ra, 0($fp)
	move    $t0, $fp
	lw      $fp, -4($fp)
	move    $sp, $t0
	li      $v0, 10
	syscall
