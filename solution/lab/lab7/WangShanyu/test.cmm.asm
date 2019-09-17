	.data		#data segment
	.align  2
_a:	.word   0	#global variable
	.text		#text segment
_goo:	
	sw      $ra, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $fp, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	subu    $sp, $sp, 0
	addu    $fp, $sp, 8
	subu    $t0, $fp, -4		#load address of local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $t0, 5
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
	sub     $t0, $t1, $t0
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
_foo:	
	sw      $ra, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $fp, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	subu    $sp, $sp, 0
	addu    $fp, $sp, 8
	lw      $t0, 8($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	jal     _goo
	addu    $sp, $sp, 8
	sw      $v0, 0($sp)	#PUSH
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
	subu    $sp, $sp, 8
	addu    $fp, $sp, 16
	.data		#data segment
STR_35:	.asciiz "input an integer for a: "	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_35
	syscall
	la      $t0, _a		#load address of global variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $v0, 5
	syscall
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	sw      $v0, 0($t0)	#Store value to address of lhs
	.data		#data segment
STR_36:	.asciiz "input a bool for b(1 for true, 0 for false): "	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_36
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
STR_37:	.asciiz "input an integer for c: "	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_37
	syscall
	subu    $t0, $fp, 12		#load address of local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $v0, 5
	syscall
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	sw      $v0, 0($t0)	#Store value to address of lhs
LOOP_2:	
	lw      $t0, _a
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, -12($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $t1, 4($sp)	#POP
	addu    $sp, $sp, 4
	sge     $t0, $t1, $t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	beqz    $t0, ENDLOOP_2
	.data		#data segment
STR_38:	.asciiz "in while: "	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_38
	syscall
	.data		#data segment
STR_39:	.asciiz "a: "	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_39
	syscall
	lw      $t0, _a
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $v0, 1
	lw      $a0, 4($sp)	#POP
	addu    $sp, $sp, 4
	syscall
	.data		#data segment
STR_40:	.asciiz " "	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_40
	syscall
	.data		#data segment
STR_41:	.asciiz "b: "	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_41
	syscall
	lw      $t0, -8($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $v0, 1
	lw      $a0, 4($sp)	#POP
	addu    $sp, $sp, 4
	syscall
	.data		#data segment
STR_42:	.asciiz " "	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_42
	syscall
	.data		#data segment
STR_43:	.asciiz "c: "	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_43
	syscall
	lw      $t0, -12($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $v0, 1
	lw      $a0, 4($sp)	#POP
	addu    $sp, $sp, 4
	syscall
	.data		#data segment
STR_44:	.asciiz "\n"	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_44
	syscall
	lw      $t0, _a
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, -12($fp)	#load local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $t0, 2
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $t1, 4($sp)	#POP
	addu    $sp, $sp, 4
	div     $t1, $t0
	mflo    $t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $t1, 4($sp)	#POP
	addu    $sp, $sp, 4
	sle     $t0, $t1, $t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	beqz    $t0, ELSE_4
	subu    $t0, $fp, 8		#load address of local variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $t0, 1
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
	j       ENDIF_4
ELSE_4:	
	.data		#data segment
STR_45:	.asciiz "a is still bigger than c/2 \n"	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_45
	syscall
ENDIF_4:	
	lw      $t0, _a
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	la      $t0, _a		#load address of global variable
	lw      $t1, 4($sp)	#POP
	addu    $sp, $sp, 4
	sub     $t1, $t1, 1		#Post increment
	sw      $t1, 0($t0)	#Store value to address of lhs
	j       LOOP_2
ENDLOOP_2:	
	.data		#data segment
STR_46:	.asciiz "out while\n"	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_46
	syscall
	.data		#data segment
STR_47:	.asciiz "a: "	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_47
	syscall
	lw      $t0, _a
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $v0, 1
	lw      $a0, 4($sp)	#POP
	addu    $sp, $sp, 4
	syscall
	.data		#data segment
STR_48:	.asciiz "\n"	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_48
	syscall
	.data		#data segment
STR_49:	.asciiz "first"	#string value
	.text		#back to text segment
	.data		#data segment
STR_50:	.asciiz "fiasrst"	#string value
	.text		#back to text segment
	la      $t2, STR_49
	la      $t3, STR_50
compare_STR_49_STR_50_start:	
	lb      $t0, 0($t2)
	lb      $t1, 0($t3)
	bne     $t0, $t1, STR_49_ne_STR_50
	beq     $t0, $zero, STR_49_eq_STR_50
	addi    $t2, $t2, 1
	addi    $t3, $t3, 1
	j       compare_STR_49_STR_50_start
STR_49_ne_STR_50:	
	li      $t0, 0		#strings not equal
	j       compare_STR_49_STR_50_end
STR_49_eq_STR_50:	
	li      $t0, 1		#strings equal
	j       compare_STR_49_STR_50_end
compare_STR_49_STR_50_end:	
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	beqz    $t0, .L2		#short circuited for and
	la      $t0, _a		#load address of global variable
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $t0, 1
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t1, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	sw      $t1, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	sw      $t1, 0($t0)	#Store value to address of lhs
	li      $t0, 1
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $t1, 4($sp)	#POP
	addu    $sp, $sp, 4
	seq     $t0, $t1, $t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	lw      $t1, 4($sp)	#POP
	addu    $sp, $sp, 4
	and     $t0, $t1, $t0
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
.L2:	
	lw      $t0, 4($sp)	#POP
	addu    $sp, $sp, 4
	beqz    $t0, ELSE_5
	.data		#data segment
STR_51:	.asciiz "equals\n"	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_51
	syscall
	j       ENDIF_5
ELSE_5:	
	.data		#data segment
STR_52:	.asciiz "not equal\n"	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_52
	syscall
ENDIF_5:	
	.data		#data segment
STR_53:	.asciiz "a: "	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_53
	syscall
	lw      $t0, _a
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	li      $v0, 1
	lw      $a0, 4($sp)	#POP
	addu    $sp, $sp, 4
	syscall
	.data		#data segment
STR_54:	.asciiz "\n"	#string value
	.text		#back to text segment
	li      $v0, 4
	la      $a0, STR_54
	syscall
	li      $t0, 1
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	lw      $t0, _a
	sw      $t0, 0($sp)	#PUSH
	subu    $sp, $sp, 4
	jal     _foo
	addu    $sp, $sp, 8
	sw      $v0, 0($sp)	#PUSH
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
