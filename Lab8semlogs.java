/* Codigo: Leitores e escritores usando monitores em Java */
/* Apenas a saida do programa */

// Monitor que implementa a logica do padrao leitores/escritores
class LE {
    private int leit, escr;  
  
    // Construtor
    LE() { 
        this.leit = 0; //leitores lendo (0 ou mais)
        this.escr = 0; //escritor escrevendo (0 ou 1)
    } 
  
    // Entrada para leitores
    public synchronized void EntraLeitor (int id) {
        try { 
            while (this.escr > 0) {
            wait();  //bloqueia pela condicao logica da aplicacao 
        }
        this.leit++;  //registra que ha mais um leitor lendo
        } catch (InterruptedException e) { }
    }
  
    // Saida para leitores
    public synchronized void SaiLeitor (int id) {
        this.leit--; //registra que um leitor saiu
        if (this.leit == 0) 
            this.notify(); //libera escritor (caso exista escritor bloqueado)
    }
  
    // Entrada para escritores
    public synchronized void EntraEscritor (int id) {
        try { 
            while ((this.leit > 0) || (this.escr > 0)) {
                wait();  //bloqueia pela condicao logica da aplicacao 
            }
        this.escr++; //registra que ha um escritor escrevendo
        } catch (InterruptedException e) { }
    }
  
    // Saida para escritores
    public synchronized void SaiEscritor (int id) {
        this.escr--; //registra que o escritor saiu
        notifyAll(); //libera leitores e escritores (caso existam leitores ou escritores bloqueados)
    }
}

// Leitor - le a variavel e a imprime na tela indicando se e um numero primo ou nao
class Leitor extends Thread {
    int id; //identificador da thread
    LE monitor;//objeto monitor para coordenar a lógica de execução das threads

    // Construtor
    Leitor (int id, LE m) {
        this.id = id;
        this.monitor = m;
    }

    // Método executado pela thread
    public synchronized void run () {
        int primo = 0; //variavel auxiliar para identificar primos
        this.monitor.EntraLeitor(this.id);
        System.out.printf("Valor lido: %d, ", Global.variavel);
        
        //verifica primos
        if (Global.variavel%2 == 0 ){
            if(Global.variavel == 2)
                System.out.printf("numero primo\n");
            else 
                System.out.printf("numero nao primo\n");
        }
        else{
            for(int i=0; i<Math.sqrt(Global.variavel); i++){
                if(Global.variavel%i == 0)
                    primo = 1;
            }
            if(primo == 1)
                System.out.println("numero primo\n");
            else
                System.out.println("numero nao primo\n");
        }
        this.monitor.SaiLeitor(this.id);
    }
}

// Escritor - modifica a variavel escrevendo o valor do seu identificador de thread
class Escritor extends Thread {
    int id; //identificador da thread
    LE monitor; //objeto monitor para coordenar a lógica de execução das threads

    // Construtor
    Escritor (int id, LE m) {
        this.id = id;
        this.monitor = m;
    }

    // Método executado pela thread
    public synchronized void run () {
        this.monitor.EntraEscritor(this.id); 
        Global.variavel = this.id; //escreve o id na variavel global
        this.monitor.SaiEscritor(this.id); 
    }
}

// Leitor e Escritor - Primeiro le e a variavel e a imprime na tela indicando se e um valor par ou impar. Depois modifica a variavel escrevendo o dobro do seu valor atual.
class LeitorEscritor extends Thread {
    int id; //identificador da thread
    LE monitor; //objeto monitor para coordenar a lógica de execução das threads

    // Construtor
    LeitorEscritor (int id, LE m) {
        this.id = id;
        this.monitor = m;
    }

    // Método executado pela thread
    public synchronized void run () {
        this.monitor.EntraLeitor(this.id);
        System.out.printf("Valor lido: %d, ", Global.variavel);
        //verifica par ou impar
        if (Global.variavel%2 == 0)
            System.out.println("variavel par");
        else 
            System.out.println("variavel impar");
        this.monitor.SaiLeitor(this.id);
        this.monitor.EntraEscritor(this.id); 
        Global.variavel = 2*Global.variavel; //escreve o id na variavel global
        this.monitor.SaiEscritor(this.id); 
    }
}

//variavel central vista por todas as threads
class Global{
    static int variavel = 0;
}

// Classe principal
class Main {
    static final int L = 4; //qualquer valor >=1
    static final int E = 3; //qualquer valor >=1
    static final int LeEs = 3; //qualquer valor >=1

    public static void main (String[] args) {
        int i;
        LE monitor = new LE();            // Monitor (objeto compartilhado entre leitores e escritores)
        Leitor[] l = new Leitor[L];       // Threads leitores
        Escritor[] e = new Escritor[E];   // Threads escritores
        LeitorEscritor[] lees = new LeitorEscritor[LeEs];   // Threads leitora e escritora

        for (i=0; i<L; i++) {
            l[i] = new Leitor(i+1, monitor);
            l[i].start(); 
        }
        for (i=0; i<E; i++) {
            e[i] = new Escritor(i+1, monitor);
            e[i].start(); 
        }
        for (i=0; i<E; i++) {
            lees[i] = new LeitorEscritor(i+1, monitor);
            lees[i].start(); 
        }
    }
}
