int f(->int)() {
    int a(int);
    a(int) = 2;
    return a(int);
}

int a(global int);
int b(global int);
void main(->void)() {
    int c(int);
    a(global int) = 3;
    b(global int) = (a(global int) + 3);
    c(int) = (a(global int) / b(global int));
}

