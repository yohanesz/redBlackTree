

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// import Enum.Categoria;
// import View.template;
// import View.view;

public class Gerenciamento {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    Scanner read = new Scanner(System.in);
    private List<Quarto> quartos = new ArrayList<>();

    public void initQuartos() {
        for (int i = 1; i <= 6; i++) {
            quartos.add(new Quarto(i, Categoria.ECONOMICO));
        }
        for (int i = 7; i <= 10; i++) {
            quartos.add(new Quarto(i, Categoria.LUXO));
        }
    }
    
    Tree treeClass = new Tree(quartos);
    
    public boolean init() { 
        
        initQuartos();

        while (true) {
            template.title("HOTEL SYSTEM");
            view.showMenu();
            
            int option = -1;
            try {
                option = template.readInt("Escolha uma opção");
                
            } catch (Exception e) {
                read.nextLine(); 
                continue; 
            }

            switch (option) {
                case 1:
                    reservarQuarto();
                    break;
                case 2:
                    cancelarReserva();
                    break;
                case 3:
                    historicoReservas();
                    break;
                case 4:
                    consultarReservasCPF();
                    break;
                case 5:
                    verificarDisponibilidade();
                    break;
                case 6:
                    listarReservas();
                    break;
                case 7:
                    relatorio();
                    break;
                case 8:
                    System.out.println("Encerrando o programa...");
                    return false; 
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        }
    
            
    }
        
    public void reservarQuarto() {
        template.clear();
        template.title("NOVA RESERVA");


        System.out.print("| CPF:");
        String cpf = read.nextLine();

        System.out.print("| Nome:");
        String nome = read.nextLine();

        System.out.print("| Quarto:");
        int numQuarto = read.nextInt();
        read.nextLine();

        System.out.print("| Data de check-in (dd/MM/yyyy): ");
        LocalDate checkIn = LocalDate.parse(read.nextLine(), formatter);

        System.out.print("| Data de check-out (dd/MM/yyyy): ");
        LocalDate checkOut = LocalDate.parse(read.nextLine(), formatter);

        Quarto selectedRoom = quartos.stream()
                .filter(q -> q.getNumQuarto() == numQuarto)
                .findFirst()
                .orElse(null);

        if (selectedRoom != null) {
            treeClass.inserir(cpf, selectedRoom, nome, checkIn, checkOut);
            template.title("RESERVADO COM SUCESSO!");
            template.pressToContinue();
        } else {
            template.warning("QUARTO INVÁLIDO");
        }
    }

    public void cancelarReserva() {
        template.title("CANCELAMENTO DE RESERVA");
        String cpf = template.readString("cpf");
        treeClass.cancelReservation(cpf);
    }

    public void historicoReservas() {
        treeClass.showHistory();
    }

    public void consultarReservasCPF() {
        template.title("CONSULTAR RESERVA POR CPF");
    
        
        String cpf = template.readString("cpf");
        treeClass.getByCPF(cpf);
    }
    

    public void verificarDisponibilidade() {
        template.title("QUARTOS DISPONÍVEIS");

        LocalDate checkIn = LocalDate.parse(template.readString("Check-in"), formatter);
        LocalDate checkOut = LocalDate.parse(template.readString("Check-out"), formatter);
        treeClass.showAvailableRooms(checkIn, checkOut);
    }

    public void listarReservas() {
        treeClass.listReservationInOrder();
    }

    public void relatorio() {

        template.title("RELATÓRIO");

        template.alignLeft("[1] - Ocupação");
        template.alignLeft("[2] - Estatística dos quartos");
        template.alignLeft("[3] - Cancelamentos");
        template.Line();
        int option = template.readInt("Escolha uma opção");

        switch (option) {
            case 1:
                template.title("OCUPAÇÃO");
                LocalDate start = LocalDate.parse(template.readString("Data inícial"), formatter);
                LocalDate end = LocalDate.parse(template.readString("Data final"), formatter);

                double Occupation = treeClass.calculateOccupancyRate(start, end);
                template.Line();
                template.alignLeft("Ocupação: " + Occupation + "% ocupado");
                template.Line();

                template.pressToContinue();
                break;

            case 2:
                treeClass.roomStatistics();
                break;

            case 3:
                template.title("CANCELAMENTOS");
                LocalDate startDate = LocalDate.parse(template.readString("Data inícial"), formatter);
                LocalDate endDate = LocalDate.parse(template.readString("Data final"), formatter);
                long cancelamentos = treeClass.calculateCancellations(startDate, endDate);

                template.alignLeft("Total: " + cancelamentos);
                template.Line();
                template.pressToContinue();
                break;

            default:
                System.out.println("Opção inválida!");
        }
            
    }


    
}
