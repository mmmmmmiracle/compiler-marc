int max(global int offset: 0 size: 4);
struct node(struct offset: 0 size: 8){
    int left(int offset: 0 size: 4);
    int right(int offset: 0 size: 4);
};

struct node(struct offset: 0 size: 8) n(global node offset: 0 size: 8);
struct tree(struct offset: 0 size: 12){
    struct node(struct offset: 0 size: 8) root(node offset: 0 size: 8);
    int size(int offset: 0 size: 4);
};

int foo(int,int->int localSize: 8 paramSize: 8)(int a(int offset: -4 size: 4), int b(int offset: -8 size: 4)) {
    int c(int offset: 8 size: 4);
    int d(int offset: 12 size: 4);
    c(int offset: 8 size: 4) = b(int offset: -8 size: 4);
    d(int offset: 12 size: 4) = (a(int offset: -4 size: 4) + b(int offset: -8 size: 4));
    return c(int offset: 8 size: 4);
}

void main(->void localSize: 36 paramSize: 0)() {
    int a(int offset: 8 size: 4);
    bool b(bool offset: 12 size: 4);
    int test(int offset: 16 size: 4);
    struct node(struct offset: 0 size: 8) n(node offset: 20 size: 8);
    struct tree(struct offset: 0 size: 12) t(tree offset: 28 size: 12);
    int after(int offset: 40 size: 4);
    max(global int offset: 0 size: 4) = 100;
    test(int offset: 16 size: 4) = (n(node offset: 20 size: 8)).left(int offset: 0 size: 4);
    cout << "a: ";
    cin >> a(int offset: 8 size: 4);
    a(int offset: 8 size: 4) = (a(int offset: 8 size: 4) + max(global int offset: 0 size: 4));
    cout << "a = ";
    cout << a(int offset: 8 size: 4);
    foo(int,int->int localSize: 8 paramSize: 8)(a(int offset: 8 size: 4), test(int offset: 16 size: 4));
}

