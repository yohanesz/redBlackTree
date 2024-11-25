// import Enum.Cor;
// import View.template;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

public class Tree {

    private static List<Quarto> rooms;
    private static final double limitOcupattion = 0.9;
    private Cliente raiz;
    private Cliente raizHistorico;

    public Tree(List<Quarto> ListOfRooms) {
        raiz = null;
        raizHistorico = null;
        rooms = ListOfRooms;
    }

    public void getByCPF(String cpf) {

        Cliente reserva = find(raiz, cpf); 
    
        if (reserva != null) {
            template.title("RESERVA");
            template.alignLeft("Nome: " + reserva.getNome());
            template.alignLeft("Quarto: " + String.valueOf(reserva.getQuarto().getNumQuarto()));
            template.alignLeft("Check-in: " + reserva.getCheckIn().toString());
            template.alignLeft("Check-out: " + reserva.getCheckOut().toString());
            template.Line();
            template.pressToContinue(); 

        } else {
            template.warning("RESERVA NÃO ENCONTRADA");
        }
    
    }

    public void inserir(String cpf, Quarto quarto, String nome, LocalDate checkIn, LocalDate checkOut) {
        if (!checkAvailability(quarto, checkIn, checkOut)) {
            template.warning("QUARTO INDISPONÍVEL NA DATA SELECIONADA");
            return;
        }
        Cliente novoNodo = new Cliente(cpf, quarto, nome, checkIn, checkOut);
        raiz = insertNode(raiz, novoNodo);
        fixInsert(novoNodo);
        
        checkOccupancyRate();
    }
    
    public void checkOccupancyRate() {
        long quartosOcupados = rooms.stream()
            .filter(this::checkOccupancyRate)
            .count();

        double taxaOcupacao = (double) quartosOcupados / rooms.size();

        if (taxaOcupacao >= limitOcupattion) {
            System.out.println("ALERTA: Taxa de ocupação atingiu " + (taxaOcupacao * 100) + "%.");
        } else {
            System.out.println("Taxa de ocupação atual: " + (taxaOcupacao * 100) + "%.");
        }
    }

    private boolean checkOccupancyRate(Quarto quarto) {
        return !checkAvailability(quarto, LocalDate.now(), LocalDate.now().plusDays(1));
    }


    private boolean checkAvailability(Quarto quarto, LocalDate checkIn, LocalDate checkOut) {
        return checkAvailabilityRecursive(raiz, quarto, checkIn, checkOut);
    }


    private Cliente insertNode(Cliente atual, Cliente novoNodo) {
        if (atual == null) {
            return novoNodo; 
        }
        
        if (novoNodo.getCpf().compareTo(atual.getCpf()) < 0) {
            atual.setEsq(insertNode(atual.getEsq(), novoNodo)); 
            atual.getEsq().setPai(atual); 
        } else if (novoNodo.getCpf().compareTo(atual.getCpf()) > 0) {
            atual.setDir(insertNode(atual.getDir(), novoNodo)); 
            atual.getDir().setPai(atual);
        }
        return atual; 
    }

    private Cliente getMin(Cliente nodo) {
	    while (nodo.getEsq() != null) {
	        nodo = nodo.getEsq();
	    }
	    return nodo;
	}
    

    private void fixInsert(Cliente nodo) {
        while (nodo != raiz && nodo.getPai() != null && nodo.getPai().getCor() == Cor.VERMELHO) {
            Cliente pai = nodo.getPai();
            Cliente avo = pai != null ? pai.getPai() : null;

            if (avo != null && pai == avo.getEsq()) {
                Cliente tio = avo.getDir();

                if (tio != null && tio.getCor() == Cor.VERMELHO) {
                    pai.setCor(Cor.PRETO);
                    tio.setCor(Cor.PRETO);
                    avo.setCor(Cor.VERMELHO);
                    nodo = avo;
                } else {
                    if (nodo == pai.getDir()) {
                        nodo = pai;
                        leftRotate(nodo);
                    }

                    pai.setCor(Cor.PRETO);
                    avo.setCor(Cor.VERMELHO);
                    rightRotate(avo);
                }
            } else if (avo != null) {
                Cliente tio = avo.getEsq();

                if (tio != null && tio.getCor() == Cor.VERMELHO) {
                    pai.setCor(Cor.PRETO);
                    tio.setCor(Cor.PRETO);
                    avo.setCor(Cor.VERMELHO);
                    nodo = avo;
                } else {
                    if (nodo == pai.getEsq()) {
                        nodo = pai;
                        rightRotate(nodo);
                    }

                    pai.setCor(Cor.PRETO);
                    avo.setCor(Cor.VERMELHO);
                    leftRotate(avo);
                }
            }
        }
        if (raiz != null) {
            raiz.setCor(Cor.PRETO);
        }
    }

    private void leftRotate(Cliente nodo) {
        Cliente novoNodo = nodo.getDir();
        nodo.setDir(novoNodo.getEsq());

        if (novoNodo.getEsq() != null) {
            novoNodo.getEsq().setPai(nodo);
        }

        novoNodo.setPai(nodo.getPai());

        if (nodo.getPai() == null) {
            raiz = novoNodo;
        } else if (nodo == nodo.getPai().getEsq()) {
            nodo.getPai().setEsq(novoNodo);
        } else {
            nodo.getPai().setDir(novoNodo);
        }

        novoNodo.setEsq(nodo);
        nodo.setPai(novoNodo);
    }

    private void rightRotate(Cliente nodo) {
        Cliente novoNodo = nodo.getEsq();
        nodo.setEsq(novoNodo.getDir());

        if (novoNodo.getDir() != null) {
            novoNodo.getDir().setPai(nodo);
        }

        novoNodo.setPai(nodo.getPai());

        if (nodo.getPai() == null) {
            raiz = novoNodo;
        } else if (nodo == nodo.getPai().getDir()) {
            nodo.getPai().setDir(novoNodo);
        } else {
            nodo.getPai().setEsq(novoNodo);
        }

        novoNodo.setDir(nodo);
        nodo.setPai(novoNodo);
    }
    
    public void showTree() {
		if (raiz == null) {
			System.out.println("A árvore está vazia");
		} else {
			showTreeRecursive(raiz, "", true);
		}
	}
	
	private void showTreeRecursive(Cliente nodo, String prefixo, boolean isFilhoDireito) {
		if (nodo != null) {
			System.out.println(prefixo + (isFilhoDireito ? "|---" : "|---") + nodo.getCpf() + " (" + nodo.getCor() + ")");
			String novoPrefixo = prefixo + (isFilhoDireito ? " " : "|");
			showTreeRecursive(nodo.getDir(), novoPrefixo, true);
			showTreeRecursive(nodo.getEsq(), novoPrefixo, false);
		}
	}

	
	public void cancelReservation(String cpf) {
	    Cliente clienteParaCancelar = find(raiz, cpf);
	    if (clienteParaCancelar == null) {
            template.warning("NENHUMA RESERVA ENCONTRADA");
	        return;
	    }

	    Cliente copiaParaHistorico = new Cliente(
	        clienteParaCancelar.getCpf(),
	        clienteParaCancelar.getQuarto(),
	        clienteParaCancelar.getNome(),
	        clienteParaCancelar.getCheckIn(),
	        clienteParaCancelar.getCheckOut()
	    );

	    System.out.println("Adicionando ao histórico: " + clienteParaCancelar.getCpf());
	    
	    raizHistorico = insertNode(raizHistorico, copiaParaHistorico);
	    fixInsertHistorico(copiaParaHistorico);

	    if (find(raizHistorico, cpf) != null) {
	        System.out.println("Reserva adicionada ao histórico com sucesso.");
	    } else {
	        System.out.println("Erro ao adicionar reserva ao histórico.");
	    }

	    // Remove a reserva da árvore principal
	    raiz = removeNode(raiz, clienteParaCancelar);
	    System.out.println("Reserva do cliente com CPF " + cpf + " foi cancelada e movida para o histórico.");
	}


	
	private void fixInsertHistorico(Cliente nodo) {
	    fixInsert(nodo);
	    if (raizHistorico != null) {
	        raizHistorico.setCor(Cor.PRETO);
	    }
	}

	
	public void showHistory() {
	    if (raizHistorico == null) {
            template.warning("NENHUMA RESERVA ENCONTRADA");
            
	    } else {
            template.title("RESERVAS CANCELADAS");
	        showHistoryDetails(raizHistorico);  
	    }
	}

    private long countCancellationsInPeriod(Cliente nodo, LocalDate inicio, LocalDate fim) {
        if (nodo == null) {
            return 0;
        }

        long cancelamentos = 0;

        if (!nodo.getCheckOut().isBefore(inicio) && !nodo.getCheckIn().isAfter(fim)) {
            cancelamentos++;
        }

        cancelamentos += countCancellationsInPeriod(nodo.getEsq(), inicio, fim);
        cancelamentos += countCancellationsInPeriod(nodo.getDir(), inicio, fim);

        return cancelamentos;
    }

	private void showHistoryDetails(Cliente cliente) {

	    if (cliente != null) {

            template.alignLeft("Cliente: " + cliente.getNome() + " | CPF: " + cliente.getCpf());
            template.alignLeft("Quarto: " +  String.valueOf(cliente.getQuarto().getNumQuarto()));
            template.alignLeft("Check-in: " + cliente.getCheckIn().toString());
            template.alignLeft("Check-out: " + cliente.getCheckOut().toString());
            template.Line();
	       
	        showHistoryDetails(cliente.getEsq());
	        showHistoryDetails(cliente.getDir());

            template.pressToContinue();
	    }
	}


	private Cliente removeNode(Cliente raiz, Cliente nodo) {
	    if (raiz == null) {
	        return null;
	    }

	    if (nodo.getCpf().compareTo(raiz.getCpf()) < 0) {
            raiz.setEsq(removeNode(raiz.getEsq(), nodo));
        } else if (nodo.getCpf().compareTo(raiz.getCpf()) > 0) {
            raiz.setDir(removeNode(raiz.getDir(), nodo));
        } else {
	        if (raiz.getEsq() == null || raiz.getDir() == null) {
	            Cliente filho = (raiz.getEsq() != null) ? raiz.getEsq() : raiz.getDir();

	            if (filho == null) {
	                filho = raiz;
	                raiz = null;
	            } else {
	                raiz = filho;
	            }
	        } else {
	            Cliente substituto = getMin(raiz.getDir());
	            raiz.setCpf(substituto.getCpf());
	            raiz.setQuarto(substituto.getQuarto());
	            raiz.setNome(substituto.getNome());
	            raiz.setCheckIn(substituto.getCheckIn());
	            raiz.setCheckOut(substituto.getCheckOut());

	            raiz.setDir(removeNode(raiz.getDir(), substituto));
	        }
	    }

	    if (raiz == null) {
	        return null;
	    }

	    return fixRemove(raiz);
	}

	private Cliente fixRemove(Cliente nodo) {
	    if (nodo == null) {
	        return null;
	    }

	    while (nodo != raiz && nodo.getCor() == Cor.PRETO) {
	        Cliente pai = nodo.getPai();

	        if (nodo == pai.getEsq()) {
	            Cliente irmao = pai.getDir();

	            if (irmao.getCor() == Cor.VERMELHO) {
	                irmao.setCor(Cor.PRETO);
	                pai.setCor(Cor.VERMELHO);
	                leftRotate(pai);
	                irmao = pai.getDir();
	            }

	            if ((irmao.getEsq() == null || irmao.getEsq().getCor() == Cor.PRETO) &&
	                (irmao.getDir() == null || irmao.getDir().getCor() == Cor.PRETO)) {
	                irmao.setCor(Cor.VERMELHO);
	                nodo = pai;
	            } else {
	                if (irmao.getDir() == null || irmao.getDir().getCor() == Cor.PRETO) {
	                    if (irmao.getEsq() != null) {
	                        irmao.getEsq().setCor(Cor.PRETO);
	                    }
	                    irmao.setCor(Cor.VERMELHO);
	                    rightRotate(irmao);
	                    irmao = pai.getDir();
	                }

	                irmao.setCor(pai.getCor());
	                pai.setCor(Cor.PRETO);
	                if (irmao.getDir() != null) {
	                    irmao.getDir().setCor(Cor.PRETO);
	                }
	                leftRotate(pai);
	                nodo = raiz;
	            }
	        } else {
	            Cliente irmao = pai.getEsq();

	            if (irmao.getCor() == Cor.VERMELHO) {
	                irmao.setCor(Cor.PRETO);
	                pai.setCor(Cor.VERMELHO);
	                rightRotate(pai);
	                irmao = pai.getEsq();
	            }

	            if ((irmao.getEsq() == null || irmao.getEsq().getCor() == Cor.PRETO) &&
	                (irmao.getDir() == null || irmao.getDir().getCor() == Cor.PRETO)) {
	                irmao.setCor(Cor.VERMELHO);
	                nodo = pai;
	            } else {
	                if (irmao.getEsq() == null || irmao.getEsq().getCor() == Cor.PRETO) {
	                    if (irmao.getDir() != null) {
	                        irmao.getDir().setCor(Cor.PRETO);
	                    }
	                    irmao.setCor(Cor.VERMELHO);
	                    leftRotate(irmao);
	                    irmao = pai.getEsq();
	                }

	                irmao.setCor(pai.getCor());
	                pai.setCor(Cor.PRETO);
	                if (irmao.getEsq() != null) {
	                    irmao.getEsq().setCor(Cor.PRETO);
	                }
	                rightRotate(pai);
	                nodo = raiz;
	            }
	        }
	    }

	    nodo.setCor(Cor.PRETO);
	    return nodo;
	}


	private Cliente find(Cliente nodo, String cpf) {
        if (nodo == null || nodo.getCpf().equals(cpf)) {
            return nodo; 
        }
        if (cpf.compareTo(nodo.getCpf()) < 0) {
            return find(nodo.getEsq(), cpf); 
        }
        return find(nodo.getDir(), cpf); 
    }
    
	
	public void checkReservation(String cpf) {
	    Cliente client = find(raiz, cpf);

	    if (client != null) {
            template.clear();
            template.title("RESERVA");
	        template.alignLeft("Nome: " + client.getNome());
	        template.alignLeft("CPF: " + client.getCpf());
	        template.alignLeft("Quarto: " + client.getQuarto().getNumQuarto());
	        template.alignLeft("Data Check-in: " + client.getCheckIn());
	        template.alignLeft("Data Check-out: " + client.getCheckOut());
	    } else {
            template.warning("RESERVA NÃO ENCONTRADA");
	    }
	}
	
    public List<Quarto> listAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        List<Quarto> disponiveis = new ArrayList<>();

        for (Quarto quarto : rooms) {
            if (checkAvailability(quarto, checkIn, checkOut)) {
                disponiveis.add(quarto);
            }
        }

        return disponiveis;
    }

    private boolean checkAvailabilityRecursive(Cliente nodo, Quarto quarto, LocalDate checkIn, LocalDate checkOut) {
        if (nodo == null) {
            return true;
        }

        if (nodo.getQuarto().getNumQuarto() == quarto.getNumQuarto() &&
            !(checkOut.isBefore(nodo.getCheckIn()) || checkIn.isAfter(nodo.getCheckOut()))) {
            return false; 
        }

        return checkAvailabilityRecursive(nodo.getEsq(), quarto, checkIn, checkOut) &&
               checkAvailabilityRecursive(nodo.getDir(), quarto, checkIn, checkOut);
    }
    
    public void showAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        List<Quarto> disponiveis = listAvailableRooms(checkIn, checkOut);

        if (disponiveis.isEmpty()) {
            template.warning("SEM QUARTOS DISPONÍVEIS");
        } else {
            template.title("QUARTOS DISPONÍVEIS");
            for (Quarto quarto : disponiveis) {
                template.alignLeft("[" + quarto.getNumQuarto() + "] - " + quarto.getCategoria());
            }
            template.Line();

            template.pressToContinue();
        }
    }
    
    public void listReservationInOrder() {
        if (raiz == null) {
            template.title("NÃO HÁ RESERVAS");
            return;
        }

        List<Cliente> reservas = new ArrayList<>();
        pickReservations(raiz, reservas);

        reservas = reservas.stream()
                .sorted(Comparator.comparing(Cliente::getCheckIn))
                .collect(Collectors.toList());

        template.title("RESERVAS");
        for (Cliente cliente : reservas) {

            template.alignLeft("Cliente: " + cliente.getNome() + " | CPF: " + cliente.getCpf());
            template.alignLeft("Quarto: " +  String.valueOf(cliente.getQuarto().getNumQuarto()));
            template.alignLeft("Check-in: " + cliente.getCheckIn().toString());
            template.alignLeft("Check-out: " + cliente.getCheckOut().toString());
            template.Line();
        }
        template.pressToContinue();
    }

    private void pickReservations(Cliente nodo, List<Cliente> reservas) {
        if (nodo != null) {
            pickReservations(nodo.getEsq(), reservas);
            reservas.add(nodo);
            pickReservations(nodo.getDir(), reservas);
        }
    }
    
    public double calculateOccupancyRate(LocalDate inicio, LocalDate fim) {
        long totalQuartos = rooms.size();
        if (totalQuartos == 0) {
            return 0.0;
        }

        long quartosOcupados = rooms.stream()
            .filter(quarto -> !checkAvailability(quarto, inicio, fim))
            .count();

        return (quartosOcupados / (double) totalQuartos) * 100;
    }

        public void roomStatistics() {
        List<Quarto> reservedRoom = new ArrayList<>();
        addReservedRooms(raiz, reservedRoom); 

        Map<Integer, Integer> frequencia = new HashMap<>();

        for (Quarto quarto : reservedRoom) {
            int numero = quarto.getNumQuarto();
            frequencia.put(numero, frequencia.getOrDefault(numero, 0) + 1);
        }

        int quartoMaisReservado = -1, quartoMenosReservado = -1;
        int maxReservas = Integer.MIN_VALUE, minReservas = Integer.MAX_VALUE;

        for (Map.Entry<Integer, Integer> entry : frequencia.entrySet()) {
            int numeroQuarto = entry.getKey();
            int reservas = entry.getValue();

            if (reservas > maxReservas) {
                maxReservas = reservas;
                quartoMaisReservado = numeroQuarto;
            }

            if (reservas < minReservas) {
                minReservas = reservas;
                quartoMenosReservado = numeroQuarto;
            }
        }

        
        if (!frequencia.isEmpty()) {
            template.title("QUARTOS - ESTATÍSTICA");
            template.alignLeft("MAIS POPULAR: [" + quartoMaisReservado + "] - " + maxReservas + " reservas");
            template.alignLeft("MENOS POPULAR: [" + quartoMenosReservado + "] - " + minReservas + " reservas");
            template.Line();
            template.pressToContinue();
        } else {
            template.warning("NENHUMA RESERVA ENCONTRADA");
        }
    }


    private void addReservedRooms(Cliente nodo, List<Quarto> reservedRoom) {
        if (nodo != null) {
            reservedRoom.add(nodo.getQuarto());
            addReservedRooms(nodo.getEsq(), reservedRoom);
            addReservedRooms(nodo.getDir(), reservedRoom);
        }
    }

    public long calculateCancellations(LocalDate inicio, LocalDate fim) {
        return countCancellationsInPeriod(raizHistorico, inicio, fim);
    }

   

    
}