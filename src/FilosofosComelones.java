import java.util.Random; //escanea la entrada del usuario
import java.util.Scanner; //genera numeros aleatorios

public class FilosofosComelones {
    Filosofo[] filosofos;
    Tenedor[] tenedores;
    Thread[] threads;

    Scanner scan;
    int numero;
    public static void main(String[] args) {
        FilosofosComelones obj = new FilosofosComelones(); //se crea el objeto
        obj.init(); //Se inicializa
        obj.cpc(); //Comienza las acciones de pensar y comer del filosofo
    }
    public void init(){
        //Preguntara al usuario cuantos filosofos estaran en la mesa, se asegurara que se mas de 1 y que no se coloquen caracteres erroneos
        scan = new Scanner(System.in);

        System.out.println("Filosofos Comelones");

        try {
            System.out.println("Ingresa el numero de filosofos: ");
            numero = scan.nextInt();

            if (numero < 2)
            {
                System.out.println("Debe de haber mas de un filosofo...");
                return;
            }
        }catch (Exception e){
            System.out.println("Ingresa un numero.");
        }

        //inicializamos las clases
        filosofos = new Filosofo[numero];
        tenedores = new Tenedor[numero];
        threads = new Thread[numero];

        for (int i = 0; i < numero; i++)
        {
            filosofos[i] = new Filosofo(i + 1);
            tenedores[i] = new Tenedor(i + 1);
        }
    }
    public void cpc(){
        //asigna los tenedores que deben de usarse
        for (int i = 0; i < numero; i++)
        {
            final int index = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        filosofos[index].comenzar(tenedores[index], (index - 1 > 0) ? tenedores[index - 1]: tenedores[numero - 1]);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            });
            threads[i].start();
        }
    }
    public class Tenedor{
        private int tenedorID; //referencia
        private boolean estado; //indicador si se puede usar o no el tenedor true(si) false(no)

        Tenedor(int tenedorID)
        {
            this.tenedorID = tenedorID;
            this.estado = true;
        }

        public synchronized void disponible() throws InterruptedException //Sirve para indicar que el filososfo ha dejado de uilizar el tenedor y esta libre
        {
            estado = true;
        }

        public synchronized boolean enUso(int filosofoID) throws InterruptedException //Indica que el filosofo quiso tomar el tenedor pero no lo ha logrado va a tener que esperar hasta que este libre el tenedor
        {
            int contador = 0;
            int tiempoEspera = new Random().nextInt(10) + 5;

            while (!estado) //checa si el estado es falso, si lo es hara que los filosofos esperen una cantidad de tiempo, si pasa un largo tiempo va atener que regresar el tenedor que tomo
            {
                Thread.sleep(new Random().nextInt(100) + 50);
                contador++;

                if(contador > tiempoEspera)
                {
                    return false;
                }
            }
            estado = false;
            return true; // significa que el filosofo consiguio el otro tenedor
        }
    }
    public class Filosofo{
        private int filosofoID; //referencia
        private Tenedor izq, der; //lo que el filosofo quiere

        public Filosofo(int filosofoID){
            this.filosofoID = filosofoID;
        }

        //Lo que hara el filosofo por el resto de su vida
        public void comenzar(Tenedor izq, Tenedor der) throws InterruptedException
        {
            this.izq = izq; //referencia de los tenedores
            this.der = der;

            while (true) //Ayuda a la alternacion infinita de comer y pensar
            {
                if (new Random().nextBoolean()){
                    comer();
                }else{
                    pensar();
                }
            }
        }

        public void pensar() throws InterruptedException
        {
            //Hace que el filosofo piense por cierto lapso de tiempo, hace un sleep y deja de pensar
            System.out.println("El filosofo " + filosofoID + " esta pensando.");
            Thread.sleep(new Random().nextInt(1000) + 100);
            System.out.println("El filosofo " + filosofoID + " ha dejado de pensar.");
        }

        public void comer() throws InterruptedException
        {
            boolean tenedorDerecho = false;
            boolean tenedorIzquierdo = false;

            System.out.println("El filosofo " + filosofoID + " esta hambriento y quiere comer.");
            System.out.println("El filosofo " + filosofoID + " toma el tenedor: " + izq.tenedorID); //toma el tenedor izq
            tenedorIzquierdo = izq.enUso(filosofoID);

            if (!tenedorIzquierdo)
            {
                return;
            }

            System.out.println("El filosofo " + filosofoID + " toma el tenedor: " + der.tenedorID); //toma tenedor der
            tenedorDerecho = der.enUso(filosofoID);

            if (!tenedorDerecho) //si no pudo tomar el tenedor der, va a tener que dejar el izq
            {
                izq.disponible();
                return;
            }

            System.out.println("El filosofo " + filosofoID + " esta comiendo.");
            Thread.sleep(new Random().nextInt(1000) + 100);

            izq.disponible();
            der.disponible();

            System.out.println("El filosofo " + filosofoID + " ha dejado de comer y ha regresado los tenedores.");
        }
    }



}
