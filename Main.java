import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

class TreeNode {
    //Clase nodo
    String value;
    List<TreeNode> children;
    Integer heuristicValue; //Guardamos las heuristicas ya calculadas para que no utilicen valores aleatorios distintos en un mismo nodo
    public TreeNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
        this.heuristicValue = null;
    }

    public void addChild(TreeNode child) {
        this.children.add(child);
    }
}

class Tree {
    TreeNode root;

    public Tree(String rootValue, int depth) {
        this.root = new TreeNode(rootValue); //Creamos el nodo raiz
        buildTree(root, depth, 1); //Generamos el resto del arbol recursivamente
    }

    // Construir el árbol de forma recursiva
    private void buildTree(TreeNode node, int maxDepth, int currentDepth) {
        //Checkeamos si estamos en el nodo raiz
        if (currentDepth == 1) {
            // Nodo raiz:
            // Creamos dos nodos hijos
            TreeNode leftChild = new TreeNode("L" + currentDepth);
            TreeNode rightChild = new TreeNode("R" + currentDepth);

            node.addChild(leftChild);
            node.addChild(rightChild);

            // Construimos recursivamente las ramas
            buildTree(leftChild, maxDepth, currentDepth + 1);
            buildTree(rightChild, maxDepth, currentDepth + 1);
            return;
        }
        //Checkeamos si alcanzamos la profundidad maxima
        if (currentDepth == maxDepth) {
            // Alcanzamos la profundidad maxima, asi que hemos finalizado
            return;
        }

        // Creamos un nodo hijo
        TreeNode child = new TreeNode(node.value.substring(0, 1) + (Integer.parseInt(node.value.substring(1, node.value.length())) + 1));
        node.addChild(child);

        // Construimos recursivamente la rama
        buildTree(child, maxDepth, currentDepth + 1);
    }

    // Método para mostrar el árbol por consola
    public void printTree() {
        printNode(root, 0);
    }

    // Función recursiva para mostrar el árbol
    private void printNode(TreeNode node, int level) {
        if (node == null) {
            return;
        }

        // Imprimimos el nodo con indentación según el nivel
        for (int i = 0; i < level; i++) {
            System.out.print("   ");
        }
        System.out.println(node.value);

        // Mostramos los hijos recursivamente
        for (TreeNode child : node.children) {
            printNode(child, level + 1);
        }
    }

    // Método para generar un "A" aleatorio
    public void assignRandomNode(String assignedValue) {
        List<TreeNode> nodes = new ArrayList<>();
        collectNodes(root, nodes); // Recolectar nodos
        if (nodes.size() > 1) { // Asegurarse de que haya al menos un nodo para renombrar
            Random rand = new Random();
            int randomIndex = rand.nextInt(nodes.size() - 1) + 1; // Elegir un índice aleatorio que no sea 0 (raíz)
            nodes.get(randomIndex).value = assignedValue; // Cambiar el valor del nodo
        }
    }

    // Método auxiliar para recolectar nodos en una lista
    private void collectNodes(TreeNode node, List<TreeNode> nodes) {
        if (node == null) {
            return;
        }
        nodes.add(node); // Añadir el nodo actual a la lista
        for (TreeNode child : node.children) {
            collectNodes(child, nodes); // Recolectar nodos de los hijos
        }
    }

    // Método de búsqueda heurística: Primero el mejor
    public void bestFirstSearch(String target) {
    /*Creamos una cola de prioridad para los nodos abiertos. 
     *Los elementos de la cola de prioridad son ordenados utilizando la heurística: el nodo con el valor más bajo en la función heurística será el primero en ser explorado.
    */
    PriorityQueue<TreeNode> openList = new PriorityQueue<>(Comparator.comparingInt(this::heuristic));
    List<TreeNode> closedList = new ArrayList<>(); //Creamos una lista para almacenar los nodos que ya han sido explorados.
    LinkedList<String> currentPath = new LinkedList<>(); // Lista para almacenar el camino actual
    Map<TreeNode, TreeNode> parentMap = new HashMap<>(); // Map para almacenar el nodo padre de cada nodo, la utilizaremos para reconstruir el camino desde el nodo encontrado hacia la raíz del árbol.
    int order = 1; // Contador para el orden de exploración

    openList.add(root); //Comenzamos explorando la raiz
    parentMap.put(root, null); // La raíz no tiene padre

    while (!openList.isEmpty()) {
        TreeNode currentNode = openList.poll(); //Extraemos el nodo con la menor heuristica
        closedList.add(currentNode); // Lo agregamos a la lista cerrada para no volver a procesarlo

        // Imprimir el orden en que se explora el nodo
        System.out.println("Explorando nodo " + order + ": " + currentNode.value);
        order++;
        //Checkeamos si el nodo explorado es el nodo buscado
        if (currentNode.value.equals(target)) {
            System.out.println("Encontrado el nodo: " + target);

            // Reconstruir el camino desde el nodo objetivo hacia la raíz
            LinkedList<String> path = new LinkedList<>();
            TreeNode node = currentNode;
            while (node != null) {
                path.addFirst(node.value); // Agregar cada nodo desde la solución hacia la raíz
                node = parentMap.get(node); // Obtener el padre del nodo actual
            }

            System.out.println("Camino: " + path);
            return; // Salir al encontrar el nodo
        }

        // Agregar hijos a la lista abierta y registrar sus padres
        for (TreeNode child : currentNode.children) {
            if (!closedList.contains(child) && !parentMap.containsKey(child)) { // Evitar nodos ya explorados
                openList.add(child); //Añadimos el nodo hijo a la lista abierta para ser explorado más adelante
                parentMap.put(child, currentNode); // Registrar el padre del hijo
            }
        }
    }

    System.out.println("Nodo no encontrado: " + target);
    }

    // Heurística: Simulada usando la distancia real del nodo al nodo "A" menos un valor aleatorio
    private int heuristic(TreeNode node) {
        if(node.heuristicValue!=null){
        //Si el valor ya fue calculado, entonces esta almacenado en el nodo
        int heuristic = node.heuristicValue;
        System.out.println("h(" + node.value + ") = " + heuristic);
        return heuristic;
        }
        Random rand = new Random();
        //Calculamos la distancia entre nuestro nodo y "A"
        int distance = distanceBetweenNodes("A", node.value);
        //Generamos un número aleatorio para simular que nuestra heuristica no es 100% precisa
        int randomMisjudgement = rand.nextInt(4); //aleatorio entre 0 y 3
        int heuristic = distance - randomMisjudgement; //Calculamos nuestra heuristica como la distancia real - el error aleatorio
        if(heuristic < 0){heuristic=0;} //valor heuristico positivo
        node.heuristicValue=heuristic; //almacenamos el valor heuristico en el nodo para no tener que volver a calcularlo
        System.out.println("h(" + node.value + ") = " + heuristic);
        return heuristic; // Devuelve la heurística para continuar con la busqueda
        
        
    }

    // Método para obtener la profundidad del nodo (Utilizado en función heurística)
    private int getDepth(TreeNode node) {
        int depth = 0;
        TreeNode current = node;

        // Contar hacia arriba hasta la raíz
        while (current != null) {
            depth++;
            // Buscar el padre (lo que no es trivial en esta implementación)
            current = findParent(root, current);
        }

        return depth - 1; // Restar 1 para que la raíz tenga profundidad 0
    }

    // Método auxiliar para encontrar el padre de un nodo dado (Utilizado en función heurística)
    private TreeNode findParent(TreeNode current, TreeNode child) {
        for (TreeNode node : current.children) {
            if (node == child) {
                return current; // Si el nodo actual es el padre del hijo
            }
            TreeNode parent = findParent(node, child);
            if (parent != null) {
                return parent; // Si se encontró al padre en los hijos
            }
        }
        return null; // No se encontró al padre
    }

    // Método para calcular la distancia entre dos nodos (Utilizado en función heurística)
    public int distanceBetweenNodes(String value1, String value2) {
        TreeNode node1 = findNode(root, value1);
        TreeNode node2 = findNode(root, value2);

        if (node1 == null || node2 == null) {
            System.out.println("Uno o ambos nodos no se encontraron.");
            return -1; // Retorna -1 si no se encuentra alguno de los nodos
        }

        TreeNode lca = findLowestCommonAncestor(root, node1, node2);
        int distance1 = getDepth(node1) - getDepth(lca);
        int distance2 = getDepth(node2) - getDepth(lca);
        return distance1 + distance2; // La distancia total es la suma de las distancias
    }

    // Método para encontrar un nodo por su valor (Utilizado en función heurística)
    private TreeNode findNode(TreeNode current, String value) {
        if (current == null) {
            return null;
        }
        if (current.value.equals(value)) {
            return current;
        }
        for (TreeNode child : current.children) {
            TreeNode found = findNode(child, value);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    // Método para encontrar el ancestro común más bajo (LCA) (Utilizado en función heurística)
    private TreeNode findLowestCommonAncestor(TreeNode current, TreeNode node1, TreeNode node2) {
        if (current == null || current == node1 || current == node2) {
            return current;
        }

        int count = 0;
        TreeNode temp = null;

        for (TreeNode child : current.children) {
            TreeNode res = findLowestCommonAncestor(child, node1, node2);
            if (res != null) {
                count++;
                temp = res;
            }
        }

        if (count == 2) {
            return current; // Si se encontraron ambos nodos, el ancestro común es este nodo
        }
        return temp; // Retorna el ancestro común encontrado
    }
}

public class Main {
    public static void main(String[] args) {
        int depth = 4;  // Número configurable de profundidad del árbol
        Tree tree = new Tree("B", depth); //Crear árbol con raiz "B"

        // Cambiar el nombre de un nodo aleatorio a "A"
        tree.assignRandomNode("A");

        // Mostrar el árbol por consola
        tree.printTree();

        // Búsqueda heurística para encontrar el nodo "A"
        tree.bestFirstSearch("A");

        
    }
}
