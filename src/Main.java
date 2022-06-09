package com.company;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

// класс узлов
// здесь узел (вершина графа) - это клетка, ребро - цена ступания на эту клетку
class Node{
    // нижний и правый, верхних и левых не нужно хранить, они все равно не могут быть оптимальными, мы идем в правый нижний угол
    private Node bottom;
    private Node right;
    // значение узла, сколько стоит ступить на эту клетку
    private int value;
    // номер узла
    int index;
    Node(int value, int index){
        this.value = value;
        this.index = index;
    }
    void addBottom(Node bottom){
        this.bottom = bottom;
    }
    void addRight(Node right){
        this.right = right;
    }
    Node getBottom(){
        return bottom;
    }
    Node getRight(){
        return right;
    }
    int getValue(){
        return value;
    }
    int getIndex(){
        return index;
    }
}

public class Main {

    static class Solution{
        static int field[];
        static char race[];
        static int price[][] = {{5,2,3,1},{2,2,5,2},{3,3,2,2}};
        Solution(int field[], char race[]){
            this.field = field;
            this.race = race;
        }
        public static int getResult(){
            // тут просто создается граф в виде массива объектов, заполняются значения ребер (val) и для него вызывается метод дейкстры
            Node[] arr = new Node[16];
            arr[0] = new Node(0, 0);
            for(int i = 1; i < 16; ++i){
                int val = 0;
                if(race[0] == 'H'){
                    val = price[0][field[i]];
                }
                else if(race[0] == 'S'){
                    val = price[1][field[i]];
                }
                else if(race[0] == 'W'){
                    val = price[2][field[i]];
                }
                arr[i] = new Node(val, i);
            }
            for(int i = 0; i < 16; ++i){
                //System.out.print(arr[i].getValue() + " ");
                // добавление нижнего перехода с учетом границы снизу
                if(i+4 < 16) arr[i].addBottom(arr[i+4]);
                // добавление правого перехода с учетом границы справа
                if((i+1) % 4 != 0) arr[i].addRight(arr[i+1]);
            }

            // массив всех расстояний от 0-й клетки до остальных 15-ти
            int[] distances = dijkstra(arr);
            System.out.print(distances[15] + " ");
            return distances[15];
        }
    }

    // релаксация весов графа, изменение distance для вершины v
    public static int relax(int from, int to, int curr_dist_u, int curr_dist_v, int dist_between_uv, int parent[]){
        int distance = curr_dist_v;
        if(curr_dist_v > curr_dist_u + dist_between_uv) {
            parent[to] = from;
            distance = curr_dist_u + dist_between_uv;
        }
        return distance;
    }

    // видоимзененный метод дейкстры для ориентированных графов с весами
    public static int[] dijkstra(Node[] nodes)
    {
        int[] distances = new int[16];
        distances[0] = 0;
        for(int i = 1; i < 16; ++i) distances[i] = 1000000;
        int[] parents = new int[16];
        parents[0] = 0;
        boolean[] was = new boolean[16];
        was[0] = true;
        for(int i = 0; i < 16; ++i){
            if(i+4 < 16)
                // (вершина под номером i) = u, (следующая из u вершина) = v
                // передается u, v, текущее расстояние до u, текущее расстояние до v, расстояние от u до v, массив предков
                // расстояние в данном случае - это цена прохождения по выбранной клетке
                distances[nodes[i].getBottom().getIndex()] = relax(i, nodes[i].getBottom().getIndex(), distances[i], distances[nodes[i].getBottom().getIndex()], nodes[i].getBottom().getValue(), parents);
            if((i+1) % 4 != 0)
                distances[nodes[i].getRight().getIndex()] = relax(i, nodes[i].getRight().getIndex(), distances[i], distances[nodes[i].getRight().getIndex()], nodes[i].getRight().getValue(), parents);
        }
        return distances;
    }

    public static void main(String[] args) {

        // само поле, расы
        int[] field = new int[17];
        char[] race = new char[8];
        short fieldSize = 0, raceSize = 0;
        // цена клеток в порядке: SWTP, первая строка для Human, вторая для Swamper, третья для Woodman
        short[][] price = {{5,2,3,1},{2,2,5,2},{3,3,2,2}};
        String root = System.getProperty("user.dir");
        File fileInput = new File(root + "\\input.txt");
        File fileOutput = new File(root + "\\output.txt");


        // читаем из файла input.txt, он должен существовать в папке программы, иначе никак
        try(FileReader reader = new FileReader(fileInput))
        {
            // читаем посимвольно, для S будет 0 значение поля, для W будет 1 и тд.
            // потому что порядок в строках price SWTP (0123),
            int c;
            while((c = reader.read()) != 10){
                while((char)c == ' ') c = reader.read();
                if((char)c == 'S') field[fieldSize] = 0;
                else if((char)c == 'W') field[fieldSize] = 1;
                else if((char)c == 'T') field[fieldSize] = 2;
                else if((char)c == 'P') field[fieldSize] = 3;
                fieldSize++;
            }

            while((c = reader.read()) != -1){
                while((char)c == ' ') c = reader.read();
                race[raceSize] = (char)c;
                raceSize++;
            }
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }


        for(int i = 0; i < fieldSize; ++i)
            System.out.print(field[i] + " ");
        System.out.println();
        System.out.println(race);

        // создаем объект класса Solution, получаем у него результат, результат будет и в файле output.txt и на консоле
        Solution sol = new Solution(field, race);
        int distance = sol.getResult();


        // пишем в файл
        try(FileWriter writer = new FileWriter(fileOutput))
        {
            writer.write(Integer.toString(distance));
            writer.close();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }

    }
}

