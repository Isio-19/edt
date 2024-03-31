public class main {
    public static void main(String[] args) {
        int index = 0;
        for (int i=0; i<5; i++) {
            for (index = index; index<20;) {
                if (index%5 == 0)
                    break;
                System.out.println(index++);
            }
        }
    }
}
