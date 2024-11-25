
// import View.template;

public class Main {

    public static void main(String[] args) {

        boolean shouldContinue = true; 

        while (shouldContinue) {
            Gerenciamento gr = new Gerenciamento(); 
            shouldContinue = gr.init(); 
        }

        template.clear();
        System.out.println("Programa encerrado."); 
    }
}
