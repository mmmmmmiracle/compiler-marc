int global(global int offset: 0 size: 4);
int max(int,int->int localSize: 32 paramSize: 8)(int x(int offset: -4 size: 4), int y(int offset: -8 size: 4)) {
    int g(int offset: 8 size: 4);
    int m(int offset: 12 size: 4);
    g(int offset: 8 size: 4) = global(global int offset: 0 size: 4);
    global(global int offset: 0 size: 4)++;
    if ((x(int offset: -4 size: 4) > y(int offset: -8 size: 4))) {
        int z(int offset: 16 size: 4);
        int m(int offset: 20 size: 4);
        int ne(int offset: 24 size: 4);
        if ((x(int offset: -4 size: 4) == z(int offset: 16 size: 4))) {
            int aa(int offset: 28 size: 4);
            int xx(int offset: 32 size: 4);
        }
    }
    else {
        int x(int offset: 16 size: 4);
        return y(int offset: -8 size: 4);
    }
    while ((g(int offset: 8 size: 4) >= m(int offset: 12 size: 4))) {
        if ((g(int offset: 8 size: 4) == m(int offset: 12 size: 4))) {
            int x1(int offset: 16 size: 4);
            int x2(int offset: 20 size: 4);
            int x3(int offset: 24 size: 4);
            int x4(int offset: 28 size: 4);
            int x5(int offset: 32 size: 4);
            int x6(int offset: 36 size: 4);
        }
    }
}

void main(->void localSize: 12 paramSize: 0)() {
    int a(int offset: 8 size: 4);
    int b(int offset: 12 size: 4);
    int m(int offset: 16 size: 4);
    cout << "a: ";
    cin >> a(int offset: 8 size: 4);
    cout << "b: ";
    cin >> b(int offset: 12 size: 4);
    m(int offset: 16 size: 4) = max(int,int->int localSize: 32 paramSize: 8)(a(int offset: 8 size: 4), b(int offset: 12 size: 4));
    cout << "the maximum is ";
    cout << m(int offset: 16 size: 4);
}

