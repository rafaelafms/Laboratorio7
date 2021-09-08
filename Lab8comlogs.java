/* Codigo: Leitores e escritores usando monitores em Java */

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
            System.out.printf("Leitor %d bloqueado\n", id);
            wait();  //bloqueia pela condicao logica da aplicacao 
        }
        this.leit++;  //registra que ha mais um leitor lendo
        System.out.printf("Leitor %d lendo\n", id);
        } catch (InterruptedException e) { }
    }
  
    // Saida para leitores
    public synchronized void SaiLeitor (int id) {
        this.leit--; //registra que um leitor saiu
        if (this.leit == 0) 
            this.notify(); //libera escritor (caso exista escritor bloqueado)
        System.out.printf("Leitor %d saindo\n", id);
    }
  
    // Entrada para escritores
    public synchronized void EntraEscritor (int id) {
        try { 
            while ((this.leit > 0) || (this.escr > 0)) {
                System.out.printf("Escritor %d bloqueado\n", id);
                wait();  //bloqueia pela condicao logica da aplicacao 
            }
        this.escr++; //registra que ha um escritor escrevendo
        System.out.printf("Escritor %d escrevendo\n", id);
        } catch (InterruptedException e) { }
    }
  
    // Saida para escritores
    public synchronized void SaiEscritor (int id) {
        this.escr--; //registra que o escritor saiu
        notifyAll(); //libera leitores e escritores (caso existam leitores ou escritores bloqueados)
        System.out.printf("Escritor %d saindo\n", id);
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
        System.out.printf("Valor lido da variavel pela thread Leitora %d: %d\n", this.id, Global.variavel);
        
        //verifica primos
        if (Global.variavel%2 == 0 ){
            if(Global.variavel == 2)
                System.out.printf("Numero primo\n");
            else 
                System.out.printf("Numero nao primo\n");
        }
        else{
            for(int i=0; i<Math.sqrt(Global.variavel); i++){
                if(Global.variavel%i == 0)
                    primo = 1;
            }
            if(primo == 1)
                System.out.println("Numero primo\n");
            else
                System.out.println("Numero nao primo\n");
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
        System.out.printf("Valor escrito na variavel pela thread Escritora %d: %d\n", this.id,Global.variavel);
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
        System.out.printf("Valor lido da variavel pela thread LeitoraEscritora %d: %d\n", this.id,Global.variavel);
        
        //verifica par ou impar
        if (Global.variavel%2 == 0 )
            System.out.println("Variavel par");
        else 
            System.out.println("Variavel impar");
        this.monitor.SaiLeitor(this.id);
        
        this.monitor.EntraEscritor(this.id); 
        Global.variavel = 2*Global.variavel; //escreve o id na variavel global
        System.out.printf("Valor escrito na da variavel pela thread LeitoraEscritora %d: %d\n", this.id,Global.variavel);
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
