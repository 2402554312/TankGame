public class Test {
    public static void main(String[] args) {
        C<A> ac = new C<>();
        ac.m1(new B());
    }
}

class A{}
class B extends A{}

class C <E>{
    public void m1(E e){

    }
}