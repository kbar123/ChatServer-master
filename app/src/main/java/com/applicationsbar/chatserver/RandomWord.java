package com.applicationsbar.chatserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomWord {
    public String word;
    public Random rand = new Random();
    public List<String> DrawObjectsList = new ArrayList<String>(Arrays.asList("Books", "Trophy", "Statue", "Dog", "Cat", "House", "Bicycle", "Map", "Diamond", "Toothpaste",
            "Sunglasses", "Ring", "Trees", "Tent", "Beach", "Pizza", "Newspapaer", "Skateboard", "Sandwich", "Skull", "Cake", "Shoes", "Drums", "Computer", "Giraffe",
            "Phone", "Apple", "Witch", "Snake", "Bee", "Pool", "MotorCycle", "Flag", "Coins", "Frog", "Sword", "Horse", "Bird", "Pencil", "Ship", "Turkey", "Basketball",
            "Airplane", "Crown", "Dinosaur", "Castle", "Gun", "Baby", "Fork", "Candy", "Moon", "Sushi", "Coffee", "Fish", "Flower", "TV", "Toothbrush", "Turtle", "Duck",
            "Bridge", "Ferris Wheel", "Bed", "Fire", "Crab", "Palm Tree", "Chair", "Binoculars", "Clock", "Donut", "Ceiling Fan", "Rose", "Robot", "Sky", "Dartboard",
            "Window", "Chess", "Bones", "Cave", "Telescope", "Cowboy", "Surfer", "Cave", "Train", "Cookies", "Shark", "Teeth", "Mouth", "Ladder", "Chef", "Doctor",
            "Tornado", "Farmer", "Pretzel", "Alien", "Rollercoaster", "Desk", "Bear", "Sweatshirt", "Pants", "Shirt", "	Spider", "Car", "Firefighter", "Lightning",
            "Police", "Bats", "Scuba Diver", "Rat", "Rain", "Snow", "Clown", "Worms", "Mushrooms", "Pirate", "Jumprope", "Feet", "Taco", "Camel", "Mermaid", "Feather"));


    public RandomWord()
    {
        Collections.shuffle(this.DrawObjectsList);
        int val = rand.nextInt(this.DrawObjectsList.size());
        this.word = this.DrawObjectsList.get(val);
    }

    public synchronized String GetWord()
    {
        return this.word;
    }

    public synchronized String GetNewWord()
    {
        int val = rand.nextInt(this.DrawObjectsList.size());
        this.word = this.DrawObjectsList.get(val);
        return this.word;
    }

}
