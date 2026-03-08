import java.util.*;

/**
 * ============================================================
 *  MEAL BLENDER - Nutrition Calculator (Java Console Version)
 *  200 Food Items: Vegetables, Fruits, Dairy, Indian Foods
 * ============================================================
 *
 *  CO MAPPING:
 *  CO1 - Algorithm efficiency: Binary Search for food lookup,
 *         Big-O analysis comments throughout
 *  CO2 - ADTs: FoodItem class, LinkedList for meal items,
 *         custom SinglyLinkedList implementation
 *  CO3 - Stack for undo operations, Queue for meal processing
 *  CO4 - HashMap for food database (O(1) average lookup),
 *         Java Collections (List, Map, Deque)
 *  CO5 - Practical applications: nutrition calculator, goal tracker
 *  CO6 - Full working application integrating all DS concepts
 */

// ============================================================
//  CO2: ADT - FoodItem represents one food entry
// ============================================================
class FoodItem {
    String key;
    String name;
    String use;
    String serving;
    double calories;
    double protein;
    double carbs;
    double fat;
    String category;

    // CO2: Constructor for FoodItem ADT
    public FoodItem(String key, String name, String use, String serving,
                    double calories, double protein, double carbs, double fat,
                    String category) {
        this.key      = key;
        this.name     = name;
        this.use      = use;
        this.serving  = serving;
        this.calories = calories;
        this.protein  = protein;
        this.carbs    = carbs;
        this.fat      = fat;
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("%-25s | Cal: %6.1f | Prot: %5.1fg | Carbs: %5.1fg | Fat: %5.1fg",
                name, calories, protein, carbs, fat);
    }
}

// ============================================================
//  CO2: Custom Singly Linked List to store meal items
//       Operations: insert O(1), traverse O(n), delete O(n)
// ============================================================
class MealNode {
    FoodItem food;
    double   quantity;
    MealNode next;

    public MealNode(FoodItem food, double quantity) {
        this.food     = food;
        this.quantity = quantity;
        this.next     = null;
    }
}

class MealLinkedList {
    // CO2: Singly linked list for meal items
    private MealNode head;
    private int size;

    public MealLinkedList() {
        head = null;
        size = 0;
    }

    // CO2: Insert at end - O(n) traversal to tail
    public void addItem(FoodItem food, double quantity) {
        MealNode newNode = new MealNode(food, quantity);
        if (head == null) {
            head = newNode;
        } else {
            MealNode curr = head;
            while (curr.next != null) curr = curr.next; // traverse to tail
            curr.next = newNode;
        }
        size++;
    }

    // CO2: Delete by food key - O(n)
    public boolean removeItem(String foodKey) {
        if (head == null) return false;
        if (head.food.key.equals(foodKey)) {
            head = head.next;
            size--;
            return true;
        }
        MealNode curr = head;
        while (curr.next != null) {
            if (curr.next.food.key.equals(foodKey)) {
                curr.next = curr.next.next;
                size--;
                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    // CO2: Traverse - O(n)
    public List<MealNode> getAll() {
        List<MealNode> result = new ArrayList<>();
        MealNode curr = head;
        while (curr != null) {
            result.add(curr);
            curr = curr.next;
        }
        return result;
    }

    public void clear() { head = null; size = 0; }
    public int  size()  { return size; }
    public boolean isEmpty() { return size == 0; }
}

// ============================================================
//  CO3: Stack for Undo (LIFO) - array-based implementation
//       Push O(1), Pop O(1), Peek O(1)
// ============================================================
class UndoStack<T> {
    // CO3: Array-based stack
    private Object[] data;
    private int top;
    private static final int MAX = 50;

    public UndoStack() {
        data = new Object[MAX];
        top  = -1;
    }

    public void push(T item) {
        if (top < MAX - 1) data[++top] = item;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        return (T) data[top--];
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return (T) data[top];
    }

    public boolean isEmpty() { return top == -1; }
    public int size()        { return top + 1;   }
}

// ============================================================
//  CO3: Queue for sequential meal processing (FIFO)
//       Enqueue O(1), Dequeue O(1) - circular array queue
// ============================================================
class MealQueue {
    // CO3: Circular array-based queue
    private String[] queue;
    private int front, rear, count;
    private static final int CAPACITY = 20;

    public MealQueue() {
        queue = new String[CAPACITY];
        front = 0; rear = 0; count = 0;
    }

    public void enqueue(String item) {
        if (count == CAPACITY) { System.out.println("Queue full!"); return; }
        queue[rear] = item;
        rear  = (rear + 1) % CAPACITY;  // circular wrap
        count++;
    }

    public String dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        String item = queue[front];
        front = (front + 1) % CAPACITY; // circular wrap
        count--;
        return item;
    }

    public boolean isEmpty() { return count == 0; }
    public int     size()    { return count; }
}

// ============================================================
//  CO3: MinHeap Priority Queue for top-N calorie foods
//       Insert O(log n), ExtractMin O(log n)
// ============================================================
class FoodPriorityQueue {
    // CO3: Min-heap by calories
    private List<double[]> heap; // [calories, index]
    private List<FoodItem> items;

    public FoodPriorityQueue() {
        heap  = new ArrayList<>();
        items = new ArrayList<>();
    }

    public void insert(FoodItem f) {
        items.add(f);
        int idx = items.size() - 1;
        heap.add(new double[]{f.calories, idx});
        siftUp(heap.size() - 1);
    }

    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (heap.get(i)[0] < heap.get(parent)[0]) {
                Collections.swap(heap, i, parent);
                i = parent;
            } else break;
        }
    }

    public FoodItem extractMin() {
        if (heap.isEmpty()) return null;
        int    minIdx = (int) heap.get(0)[1];
        int    last   = heap.size() - 1;
        heap.set(0, heap.get(last));
        heap.remove(last);
        if (!heap.isEmpty()) siftDown(0);
        return items.get(minIdx);
    }

    private void siftDown(int i) {
        int n = heap.size();
        while (true) {
            int smallest = i, l = 2*i+1, r = 2*i+2;
            if (l < n && heap.get(l)[0] < heap.get(smallest)[0]) smallest = l;
            if (r < n && heap.get(r)[0] < heap.get(smallest)[0]) smallest = r;
            if (smallest == i) break;
            Collections.swap(heap, i, smallest);
            i = smallest;
        }
    }

    public boolean isEmpty() { return heap.isEmpty(); }
    public int size()        { return heap.size();    }
}

// ============================================================
//  MAIN APPLICATION CLASS
// ============================================================
public class MealBlender {

    // CO4: HashMap as primary food database - O(1) average lookup
    //      Key = food identifier, Value = FoodItem object
    private static Map<String, FoodItem> foodDatabase = new HashMap<>();

    // CO4: Java Collections - sorted list for binary search demo
    private static List<String> sortedFoodNames = new ArrayList<>();

    // CO2: Linked list holds the current meal
    private static MealLinkedList currentMeal = new MealLinkedList();

    // CO3: Stack for undo last add operation
    private static UndoStack<String> undoStack = new UndoStack<>();

    // CO4: Java Deque as both stack and queue
    private static Deque<String> recentSearches = new ArrayDeque<>();

    static {
        // ============================================================
        //  CO4: Populating HashMap - O(1) per insertion, O(n) total
        //       food database with 200 items across 4 categories
        // ============================================================

        // --- VEGETABLES (60) ---
        addFood("spinach","Spinach","Iron rich, immunity boost","cup",7,0.9,1.1,0.1,"veg");
        addFood("broccoli","Broccoli","Vitamin C, bone health","cup",31,2.6,6,0.3,"veg");
        addFood("carrot","Carrot","Good for eyesight","carrot",25,0.6,6,0.1,"veg");
        addFood("tomato","Tomato","Heart health, lycopene","tomato",22,1.1,5,0.2,"veg");
        addFood("cucumber","Cucumber","95% water, hydrating","cucumber",32,1.4,8,0.2,"veg");
        addFood("bell_pepper","Bell Pepper","Vitamin C boost","pepper",31,1,7,0.3,"veg");
        addFood("cauliflower","Cauliflower","Low carb, high fiber","cup",25,2,5,0.3,"veg");
        addFood("peas","Peas","Protein, fiber rich","cup",117,7.9,21,0.6,"veg");
        addFood("corn","Corn","Energy booster","gram",0.86,0.03,0.19,0.01,"veg");
        addFood("green_beans","Green Beans","Vitamin K source","cup",44,2,10,0.4,"veg");
        addFood("zucchini","Zucchini","Weight management","cup",20,1.5,4,0.4,"veg");
        addFood("eggplant","Eggplant","Brain health support","cup",35,0.8,9,0.2,"veg");
        addFood("cabbage","Cabbage","Digestive health","cup",22,1,5,0.1,"veg");
        addFood("kale","Kale","Superfood, vitamin rich","cup",33,2.9,6,0.6,"veg");
        addFood("lettuce","Lettuce","Low calorie, hydrating","cup",5,0.5,1,0.1,"veg");
        addFood("onion","Onion","Anti-inflammatory","onion",44,1.2,10,0.1,"veg");
        addFood("garlic","Garlic","Immune booster","clove",4,0.2,1,0,"veg");
        addFood("potato","Potato","Energy, potassium","potato",163,4.3,37,0.2,"veg");
        addFood("sweet_potato","Sweet Potato","Vitamin A rich","potato",112,2,26,0.1,"veg");
        addFood("beet","Beet","Blood pressure control","beet",58,2.2,13,0.2,"veg");
        addFood("celery","Celery","Hydrating, low calorie","cup",16,0.7,3,0.2,"veg");
        addFood("radish","Radish","Detoxifying properties","cup",19,0.8,4,0.1,"veg");
        addFood("asparagus","Asparagus","Folate rich","cup",27,3,5,0.2,"veg");
        addFood("leek","Leek","Heart health support","cup",54,1.3,13,0.3,"veg");
        addFood("pumpkin","Pumpkin","Eye health, vitamin A","cup",30,1.2,7,0.1,"veg");
        addFood("mushroom","Mushroom","Vitamin D source","cup",15,2.2,2.3,0.2,"veg");
        addFood("brussels_sprouts","Brussels Sprouts","Fiber, vitamin K","cup",56,4,11,0.8,"veg");
        addFood("artichoke","Artichoke","Liver health support","artichoke",60,4.2,13,0.2,"veg");
        addFood("okra","Okra","Blood sugar control","cup",33,2,7,0.2,"veg");
        addFood("bitter_gourd","Bitter Gourd","Diabetes management","cup",17,0.8,3.7,0.2,"veg");
        addFood("bottle_gourd","Bottle Gourd","Cooling, weight loss","cup",14,0.6,3.4,0.02,"veg");
        addFood("ridge_gourd","Ridge Gourd","Blood purifier","cup",20,1.2,4.5,0.2,"veg");
        addFood("drumstick","Drumstick","Bone health support","cup",37,2,8.5,0.2,"veg");
        addFood("fenugreek","Fenugreek","Digestive aid","cup",6,0.9,0.6,0.1,"veg");
        addFood("coriander","Coriander","Antioxidant rich","cup",1,0.1,0.1,0,"veg");
        addFood("mint","Mint","Digestive aid, fresh","cup",6,0.6,1.2,0.1,"veg");
        addFood("parsley","Parsley","Vitamin K rich","cup",22,1.8,3.8,0.5,"veg");
        addFood("chili","Chili","Metabolism booster","pepper",18,0.9,4,0.2,"veg");
        addFood("ginger","Ginger","Anti-inflammatory","gram",0.8,0.02,0.18,0.01,"veg");
        addFood("curry_leaves","Curry Leaves","Digestive health","gram",1.08,0.06,0.18,0.01,"veg");
        addFood("capsicum","Capsicum","Antioxidant rich","pepper",20,0.9,4.6,0.2,"veg");
        addFood("baby_corn","Baby Corn","Low calorie snack","cup",30,1.5,6,0.3,"veg");
        addFood("lotus_root","Lotus Root","Vitamin C rich","cup",74,2.6,17.2,0.1,"veg");
        addFood("snake_gourd","Snake Gourd","Digestive health","cup",18,1.1,4.2,0.1,"veg");
        addFood("ivy_gourd","Ivy Gourd","Blood sugar control","cup",19,1.4,3.7,0.2,"veg");
        addFood("cluster_beans","Cluster Beans","Fiber rich","cup",49,3.2,10.8,0.5,"veg");
        addFood("broad_beans","Broad Beans","Protein source","cup",187,12.5,33,0.7,"veg");
        addFood("amaranth","Amaranth","Iron rich greens","cup",23,2.1,4.6,0.3,"veg");
        addFood("methi","Methi","Blood sugar control","cup",49,4.4,6.4,0.9,"veg");
        addFood("dill","Dill","Digestive health","cup",4,0.3,0.6,0.1,"veg");
        addFood("moringa","Moringa","Super nutritious","cup",13,2,1.8,0.3,"veg");
        addFood("banana_flower","Banana Flower","Digestive health","cup",51,1.6,9.9,0.6,"veg");
        addFood("banana_stem","Banana Stem","Detoxifying","cup",20,0.8,4,0.2,"veg");
        addFood("turnip","Turnip","Vitamin C source","cup",36,1.2,8,0.1,"veg");
        addFood("parsnip","Parsnip","Fiber rich","cup",100,1.6,24,0.4,"veg");
        addFood("fennel","Fennel","Digestive aid","cup",27,1.1,6,0.2,"veg");
        addFood("bok_choy","Bok Choy","Bone health support","cup",9,1.1,1.5,0.1,"veg");
        addFood("watercress","Watercress","Vitamin K powerhouse","cup",4,0.8,0.4,0,"veg");
        addFood("arugula","Arugula","Folate rich","cup",5,0.5,0.7,0.1,"veg");
        addFood("kohlrabi","Kohlrabi","Vitamin C source","cup",36,2.3,8,0.1,"veg");

        // --- FRUITS (50) ---
        addFood("apple","Apple","Heart health, fiber","apple",95,0.5,25,0.3,"fruit");
        addFood("banana","Banana","Energy, potassium","banana",105,1.3,27,0.4,"fruit");
        addFood("mango","Mango","Vitamin A rich","mango",202,2.8,50,1.3,"fruit");
        addFood("orange","Orange","Vitamin C boost","orange",62,1.2,15,0.2,"fruit");
        addFood("grapes","Grapes","Antioxidants, heart health","gram",0.69,0.007,0.18,0.002,"fruit");
        addFood("strawberry","Strawberry","Vitamin C, skin health","gram",0.32,0.007,0.077,0.003,"fruit");
        addFood("watermelon","Watermelon","Hydrating, lycopene","cup",46,0.9,12,0.2,"fruit");
        addFood("pineapple","Pineapple","Digestive enzyme","cup",82,0.9,22,0.2,"fruit");
        addFood("papaya","Papaya","Digestive health","cup",62,0.7,16,0.4,"fruit");
        addFood("guava","Guava","Vitamin C powerhouse","guava",37,1.4,8,0.5,"fruit");
        addFood("pomegranate","Pomegranate","Antioxidant rich","cup",144,3,32,2,"fruit");
        addFood("kiwi","Kiwi","Vitamin C, digestive","kiwi",42,0.8,10,0.4,"fruit");
        addFood("pear","Pear","Fiber rich fruit","pear",101,0.6,27,0.2,"fruit");
        addFood("peach","Peach","Skin health support","peach",58,1.4,14,0.4,"fruit");
        addFood("plum","Plum","Bone health support","plum",30,0.5,8,0.2,"fruit");
        addFood("cherry","Cherry","Anti-inflammatory","gram",0.63,0.01,0.16,0.002,"fruit");
        addFood("blueberry","Blueberry","Brain health booster","gram",0.57,0.007,0.14,0.003,"fruit");
        addFood("raspberry","Raspberry","Fiber rich berry","gram",0.52,0.012,0.12,0.007,"fruit");
        addFood("blackberry","Blackberry","Vitamin C rich","gram",0.43,0.014,0.096,0.005,"fruit");
        addFood("lychee","Lychee","Vitamin C source","gram",0.66,0.008,0.17,0.004,"fruit");
        addFood("coconut","Coconut","Healthy fats","cup",283,2.7,12,27,"fruit");
        addFood("avocado","Avocado","Healthy fats, potassium","avocado",234,2.9,12,21,"fruit");
        addFood("fig","Fig","Digestive health","fig",47,0.5,12,0.2,"fruit");
        addFood("date","Date","Energy booster","date",20,0.2,5,0,"fruit");
        addFood("apricot","Apricot","Eye health support","apricot",17,0.5,4,0.1,"fruit");
        addFood("grapefruit","Grapefruit","Weight loss aid","grapefruit",52,0.9,13,0.2,"fruit");
        addFood("lime","Lime","Vitamin C source","lime",20,0.5,7,0.1,"fruit");
        addFood("lemon","Lemon","Detox, vitamin C","lemon",17,0.6,5,0.2,"fruit");
        addFood("melon","Melon","Hydrating fruit","cup",54,1.3,13,0.3,"fruit");
        addFood("dragon_fruit","Dragon Fruit","Antioxidant rich","cup",102,2,22,0.6,"fruit");
        addFood("passion_fruit","Passion Fruit","Vitamin C, fiber","fruit",17,0.4,4,0.1,"fruit");
        addFood("custard_apple","Custard Apple","Energy boost","fruit",236,5,59,0.6,"fruit");
        addFood("jackfruit","Jackfruit","Energy source","cup",155,2.8,38,0.5,"fruit");
        addFood("sapodilla","Sapodilla","Energy booster","fruit",83,0.4,20,1.1,"fruit");
        addFood("jamun","Jamun","Blood sugar control","gram",0.6,0.007,0.14,0.002,"fruit");
        addFood("amla","Amla","Vitamin C powerhouse","amla",44,0.9,10.2,0.6,"fruit");
        addFood("sweet_lime","Sweet Lime","Vitamin C source","lime",43,0.7,9.3,0.3,"fruit");
        addFood("pomelo","Pomelo","Vitamin C rich","cup",72,1.4,18,0.1,"fruit");
        addFood("cantaloupe","Cantaloupe","Hydrating melon","cup",60,1.5,14.4,0.3,"fruit");
        addFood("honeydew","Honeydew","Vitamin C source","cup",64,1,16,0.2,"fruit");
        addFood("tangerine","Tangerine","Vitamin C boost","tangerine",47,0.7,11.7,0.3,"fruit");
        addFood("nectarine","Nectarine","Vitamin A source","nectarine",63,1.5,15.1,0.5,"fruit");
        addFood("cranberry","Cranberry","UTI prevention","gram",0.46,0.004,0.12,0.001,"fruit");
        addFood("mulberry","Mulberry","Vitamin C rich","cup",60,2,14,0.5,"fruit");
        addFood("gooseberry","Gooseberry","Vitamin C powerhouse","cup",66,1.3,15,0.9,"fruit");
        addFood("persimmon","Persimmon","Fiber rich fruit","persimmon",118,1,31,0.3,"fruit");
        addFood("starfruit","Star Fruit","Vitamin C source","fruit",28,1,6,0.3,"fruit");
        addFood("rambutan","Rambutan","Vitamin C rich","fruit",7,0.1,1.6,0.02,"fruit");
        addFood("mangosteen","Mangosteen","Antioxidant rich","fruit",73,0.4,17.9,0.6,"fruit");
        addFood("tamarind","Tamarind","Digestive aid","cup",287,3.4,75,0.7,"fruit");

        // --- DAIRY (22) ---
        addFood("milk","Milk","Calcium, bone health","cup",149,8,12,8,"dairy");
        addFood("yogurt","Yogurt (Curd)","Probiotics, gut health","cup",154,13,17,4,"dairy");
        addFood("paneer","Paneer","Protein rich","gram",2.65,0.18,0.03,0.2,"dairy");
        addFood("ghee","Ghee","Healthy cooking fat","tablespoon",112,0,0,13,"dairy");
        addFood("butter","Butter","Cooking fat, vitamin A","tablespoon",102,0.1,0,12,"dairy");
        addFood("cheese","Cheese","Calcium, protein","slice",113,7,0.4,9,"dairy");
        addFood("cream","Cream","Cooking ingredient","tablespoon",52,0.4,0.4,5.5,"dairy");
        addFood("buttermilk","Buttermilk","Probiotics, cooling","cup",98,8,12,2.2,"dairy");
        addFood("khoya","Khoya","Indian sweets base","gram",3.57,0.25,0.26,0.28,"dairy");
        addFood("malai","Malai","Cream topping","tablespoon",52,0.4,0.4,5.5,"dairy");
        addFood("lassi","Lassi","Refreshing drink","cup",160,8,24,4,"dairy");
        addFood("shrikhand","Shrikhand","Sweet dessert","cup",180,6,32,4,"dairy");
        addFood("cottage_cheese","Cottage Cheese","High protein","cup",163,28,6,2.3,"dairy");
        addFood("mozzarella","Mozzarella","Pizza cheese","slice",85,6.3,0.6,6.3,"dairy");
        addFood("cheddar","Cheddar","Calcium rich","slice",113,7,0.4,9,"dairy");
        addFood("ricotta","Ricotta","High protein","gram",1.74,0.11,0.03,0.13,"dairy");
        addFood("cream_cheese","Cream Cheese","Spread, baking","tablespoon",51,0.9,0.4,5.1,"dairy");
        addFood("parmesan","Parmesan","Flavor enhancer","tablespoon",21,1.9,0.2,1.4,"dairy");
        addFood("greek_yogurt","Greek Yogurt","High protein yogurt","cup",100,17,6,0.7,"dairy");
        addFood("buffalo_milk","Buffalo Milk","Rich calcium source","cup",237,9,13,17,"dairy");
        addFood("goat_milk","Goat Milk","Easily digestible","cup",168,9,11,10,"dairy");
        addFood("kefir","Kefir","Probiotic drink","cup",104,9,12,2.5,"dairy");

        // --- INDIAN FOODS (68) ---
        addFood("chapati","Chapati","Whole wheat flatbread","chapati",71,2.4,14.9,0.4,"indian");
        addFood("rice_white","White Rice","Energy source","cup",205,4.3,45,0.4,"indian");
        addFood("rice_brown","Brown Rice","Fiber rich rice","cup",216,5,45,1.8,"indian");
        addFood("wheat_bread","Wheat Bread","Fiber, B vitamins","slice",79,4,13.8,1,"indian");
        addFood("dal_toor","Toor Dal","Protein, fiber","cup",198,10,36,0.8,"indian");
        addFood("dal_moong","Moong Dal","Light, digestible","cup",212,14,38,0.8,"indian");
        addFood("dal_masoor","Masoor Dal","Protein rich lentil","cup",230,18,40,0.8,"indian");
        addFood("dal_chana","Chana Dal","High protein","cup",269,15,45,4,"indian");
        addFood("dal_urad","Urad Dal","Protein, iron rich","cup",230,18,35,1,"indian");
        addFood("rajma","Rajma","Protein, fiber","cup",225,15,40,0.9,"indian");
        addFood("chole","Chole","Protein rich chickpeas","cup",269,15,45,4,"indian");
        addFood("poha","Poha","Light breakfast","cup",180,3,38,1.5,"indian");
        addFood("upma","Upma","Breakfast dish","cup",220,5,42,4,"indian");
        addFood("idli","Idli","Fermented, digestible","idli",39,2,8,0.1,"indian");
        addFood("dosa","Dosa","Crispy breakfast","dosa",168,4,28,4,"indian");
        addFood("uttapam","Uttapam","Thick pancake","uttapam",150,4,26,3,"indian");
        addFood("paratha","Paratha","Stuffed flatbread","paratha",126,3,18,4.4,"indian");
        addFood("puri","Puri","Deep fried bread","puri",85,2,11,4,"indian");
        addFood("naan","Naan","Leavened flatbread","naan",262,9,45,5,"indian");
        addFood("kulcha","Kulcha","Soft flatbread","kulcha",180,5,30,4,"indian");
        addFood("bhatura","Bhatura","Deep fried bread","bhatura",200,5,28,8,"indian");
        addFood("multigrain_bread","Multigrain Bread","High fiber bread","slice",69,3.5,11,1.1,"indian");
        addFood("oats","Oats","Heart healthy","cup",166,5.9,28.1,3.6,"indian");
        addFood("daliya","Daliya","Broken wheat","cup",151,5.5,33.2,0.5,"indian");
        addFood("khichdi","Khichdi","Comfort food","cup",180,6,35,2,"indian");
        addFood("biryani","Veg Biryani","Aromatic rice dish","cup",250,6,45,6,"indian");
        addFood("pulao","Pulao","Spiced rice","cup",220,5,42,4,"indian");
        addFood("sambar","Sambar","Lentil vegetable stew","cup",120,6,20,2,"indian");
        addFood("rasam","Rasam","Digestive soup","cup",80,3,12,2,"indian");
        addFood("lemon_rice","Lemon Rice","Tangy rice dish","cup",210,4,42,4,"indian");
        addFood("curd_rice","Curd Rice","Cooling rice dish","cup",190,6,36,3,"indian");
        addFood("pongal","Pongal","Rice lentil dish","cup",200,7,38,3,"indian");
        addFood("vada","Vada","Crispy fritter","vada",150,5,18,7,"indian");
        addFood("appam","Appam","Rice pancake","appam",110,2,22,2,"indian");
        addFood("ragi_dosa","Ragi Dosa","Calcium rich dosa","dosa",140,4,26,2,"indian");
        addFood("bajra_roti","Bajra Roti","Iron rich roti","roti",97,3.6,18.7,1.4,"indian");
        addFood("jowar_roti","Jowar Roti","Gluten free roti","roti",100,3.5,20,1.2,"indian");
        addFood("makki_roti","Makki Roti","Corn flatbread","roti",120,3,24,2,"indian");
        addFood("methi_paratha","Methi Paratha","Iron rich paratha","paratha",140,4,20,5,"indian");
        addFood("aloo_paratha","Aloo Paratha","Potato stuffed","paratha",150,3.5,22,5.5,"indian");
        addFood("paneer_paratha","Paneer Paratha","Protein rich paratha","paratha",160,6,20,6,"indian");
        addFood("stuffed_kulcha","Stuffed Kulcha","Soft stuffed bread","kulcha",200,6,32,5,"indian");
        addFood("jeera_rice","Jeera Rice","Cumin flavored","cup",210,4,43,4,"indian");
        addFood("kashmiri_pulao","Kashmiri Pulao","Fruit rice","cup",240,5,46,5,"indian");
        addFood("veg_pulao","Veg Pulao","Vegetable rice","cup",220,5,42,4,"indian");
        addFood("tamarind_rice","Tamarind Rice","Tangy rice","cup",215,4,43,4,"indian");
        addFood("coconut_rice","Coconut Rice","Aromatic coconut rice","cup",230,4,44,5,"indian");
        addFood("tomato_rice","Tomato Rice","Tangy tomato rice","cup",205,4,41,3.5,"indian");
        addFood("mint_rice","Mint Rice","Refreshing rice","cup",210,4,42,4,"indian");
        addFood("pesarattu","Pesarattu","Moong dal dosa","dosa",155,6,26,3,"indian");
        addFood("akki_roti","Akki Roti","Rice flatbread","roti",130,2.5,26,2.5,"indian");
        addFood("thepla","Thepla","Spiced flatbread","thepla",110,3,18,3,"indian");
        addFood("dhokla","Dhokla","Steamed cake","piece",160,4,32,1.5,"indian");
        addFood("khandvi","Khandvi","Gram flour rolls","piece",120,5,18,4,"indian");
        addFood("handvo","Handvo","Savory cake","piece",180,6,28,6,"indian");
        addFood("methi_thepla","Methi Thepla","Fenugreek bread","thepla",115,3.5,18,3.5,"indian");
        addFood("sabudana_khichdi","Sabudana Khichdi","Tapioca dish","cup",180,2,40,2,"indian");
        addFood("poori","Poori","Puffed bread","poori",85,2,11,4,"indian");
        addFood("aloo_puri","Aloo Puri","Potato curry with puri","serving",200,5,32,7,"indian");
        addFood("masala_dosa","Masala Dosa","Potato filled dosa","dosa",220,6,38,6,"indian");
        addFood("rava_dosa","Rava Dosa","Crispy semolina dosa","dosa",180,5,30,5,"indian");
        addFood("set_dosa","Set Dosa","Soft thick dosa","dosa",150,4,26,3,"indian");
        addFood("neer_dosa","Neer Dosa","Thin rice dosa","dosa",100,2,20,1,"indian");
        addFood("wheat_dosa","Wheat Dosa","Whole wheat dosa","dosa",140,4,25,2.5,"indian");
        addFood("oats_dosa","Oats Dosa","Fiber rich dosa","dosa",135,5,23,3,"indian");
        addFood("moong_dosa","Moong Dal Dosa","Protein rich dosa","dosa",145,7,24,2,"indian");
        addFood("adai","Adai","Multi lentil pancake","adai",170,8,28,3,"indian");
        addFood("medu_vada","Medu Vada","Crispy fritter","vada",150,5,18,7,"indian");

        // CO1: Sort food names for binary search - O(n log n)
        sortedFoodNames.addAll(
            foodDatabase.values().stream()
                .map(f -> f.name)
                .sorted()
                .collect(java.util.stream.Collectors.toList())
        );
    }

    // CO4: Helper to insert into HashMap
    private static void addFood(String key, String name, String use, String serving,
                                 double cal, double pro, double carb, double fat,
                                 String cat) {
        foodDatabase.put(key, new FoodItem(key, name, use, serving, cal, pro, carb, fat, cat));
    }

    // ============================================================
    //  CO1: Binary Search on sorted food names - O(log n)
    //       Compare with linear search - O(n)
    // ============================================================
    public static int binarySearchFood(String target) {
        int lo = 0, hi = sortedFoodNames.size() - 1;
        String t = target.toLowerCase();
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int cmp = sortedFoodNames.get(mid).toLowerCase().compareTo(t);
            if (cmp == 0)      return mid;
            else if (cmp < 0)  lo = mid + 1;
            else               hi = mid - 1;
        }
        return -1; // not found
    }

    // CO1: Linear Search - O(n) - for comparison
    public static List<FoodItem> linearSearchFood(String query) {
        List<FoodItem> results = new ArrayList<>();
        String q = query.toLowerCase();
        // CO1: O(n) - checks every item
        for (FoodItem f : foodDatabase.values()) {
            if (f.name.toLowerCase().contains(q)) {
                results.add(f);
            }
        }
        return results;
    }

    // ============================================================
    //  CO1: Bubble Sort on food items by calories - O(n²)
    // ============================================================
    public static List<FoodItem> bubbleSortByCalories(List<FoodItem> list) {
        List<FoodItem> arr = new ArrayList<>(list);
        int n = arr.size();
        // CO1: Bubble Sort - O(n²) time, O(1) space
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr.get(j).calories > arr.get(j + 1).calories) {
                    FoodItem tmp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, tmp);
                }
            }
        }
        return arr;
    }

    // ============================================================
    //  CO1: Merge Sort on food items by protein - O(n log n)
    // ============================================================
    public static List<FoodItem> mergeSortByProtein(List<FoodItem> list) {
        if (list.size() <= 1) return list;
        int mid = list.size() / 2;
        List<FoodItem> left  = mergeSortByProtein(new ArrayList<>(list.subList(0, mid)));
        List<FoodItem> right = mergeSortByProtein(new ArrayList<>(list.subList(mid, list.size())));
        return merge(left, right);
    }

    private static List<FoodItem> merge(List<FoodItem> l, List<FoodItem> r) {
        List<FoodItem> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < l.size() && j < r.size()) {
            if (l.get(i).protein >= r.get(j).protein) result.add(l.get(i++));
            else                                        result.add(r.get(j++));
        }
        while (i < l.size()) result.add(l.get(i++));
        while (j < r.size()) result.add(r.get(j++));
        return result;
    }

    // ============================================================
    //  CO5: Calculate and display nutrition totals from meal list
    //       CO2: Traverses the linked list - O(n)
    // ============================================================
    public static void calculateNutrition() {
        if (currentMeal.isEmpty()) {
            System.out.println("  [!] No items in your meal. Add food first.");
            return;
        }
        double totalCal = 0, totalPro = 0, totalCarb = 0, totalFat = 0;
        List<MealNode> nodes = currentMeal.getAll(); // CO2: linked list traversal

        System.out.println("\n" + "=".repeat(70));
        System.out.println("  NUTRITION BREAKDOWN");
        System.out.println("=".repeat(70));

        // CO3: Process each meal item using queue (FIFO order)
        MealQueue processQueue = new MealQueue();
        for (MealNode node : nodes) processQueue.enqueue(node.food.key + ":" + node.quantity);

        for (MealNode node : nodes) {
            FoodItem f  = node.food;
            double   q  = node.quantity;
            double cal  = f.calories * q;
            double pro  = f.protein  * q;
            double carb = f.carbs    * q;
            double fat  = f.fat      * q;

            System.out.printf("%n  %-25s  (%.1f %s)%n", f.name, q, f.serving);
            System.out.printf("  %-15s  %s%n", "Benefit:", f.use);
            System.out.printf("  Calories: %7.1f kcal  |  Protein: %6.1f g%n", cal, pro);
            System.out.printf("  Carbs:    %7.1f g     |  Fat:     %6.1f g%n", carb, fat);
            System.out.println("  " + "-".repeat(55));

            totalCal  += cal;
            totalPro  += pro;
            totalCarb += carb;
            totalFat  += fat;
        }

        System.out.println("\n  TOTAL NUTRITION (" + nodes.size() + " items)");
        System.out.println("  " + "=".repeat(55));
        System.out.printf("  Total Calories : %8.1f kcal%n", totalCal);
        System.out.printf("  Total Protein  : %8.1f g%n",    totalPro);
        System.out.printf("  Total Carbs    : %8.1f g%n",    totalCarb);
        System.out.printf("  Total Fat      : %8.1f g%n",    totalFat);

        // CO5: Daily goal progress bars (2000 kcal diet)
        System.out.println("\n  DAILY GOALS (2000 kcal diet)");
        System.out.println("  " + "-".repeat(55));
        printGoalBar("Calories", totalCal,  2000, "kcal");
        printGoalBar("Protein",  totalPro,    50, "g");
        printGoalBar("Carbs",    totalCarb,  300, "g");
        printGoalBar("Fat",      totalFat,    70, "g");
        System.out.println("=".repeat(70));
    }

    // CO5: Helper to print ASCII progress bar
    private static void printGoalBar(String label, double value, double goal, String unit) {
        double pct    = Math.min(value / goal * 100, 100);
        int    filled = (int)(pct / 5);   // 20 char bar
        String bar    = "[" + "#".repeat(filled) + "-".repeat(20 - filled) + "]";
        System.out.printf("  %-10s %s %5.1f/%.0f%s (%.0f%%)%n",
                label + ":", bar, value, goal, unit, pct);
    }

    // ============================================================
    //  CO5: Show top N low-calorie foods using heap - O(n log k)
    // ============================================================
    public static void showLowCalorieFoods(String category, int topN) {
        FoodPriorityQueue pq = new FoodPriorityQueue(); // CO3: min-heap
        for (FoodItem f : foodDatabase.values()) {
            if (category.equals("all") || f.category.equals(category)) {
                pq.insert(f);
            }
        }
        System.out.println("\n  Top " + topN + " lowest-calorie foods"
                + (category.equals("all") ? "" : " (" + category + ")")
                + " [Min-Heap O(log n)]");
        System.out.println("  " + "-".repeat(65));
        int count = 0;
        while (!pq.isEmpty() && count < topN) {
            FoodItem f = pq.extractMin();
            System.out.printf("  %2d. %s%n", ++count, f);
        }
    }

    // ============================================================
    //  CO5: Show top-N high-protein foods using merge sort
    // ============================================================
    public static void showHighProteinFoods(String category, int topN) {
        List<FoodItem> list = new ArrayList<>();
        for (FoodItem f : foodDatabase.values()) {
            if (category.equals("all") || f.category.equals(category)) list.add(f);
        }
        // CO1: Merge sort O(n log n)
        List<FoodItem> sorted = mergeSortByProtein(list);
        System.out.println("\n  Top " + topN + " high-protein foods"
                + (category.equals("all") ? "" : " (" + category + ")")
                + " [Merge Sort O(n log n)]");
        System.out.println("  " + "-".repeat(65));
        for (int i = 0; i < Math.min(topN, sorted.size()); i++) {
            System.out.printf("  %2d. %s%n", i + 1, sorted.get(i));
        }
    }

    // ============================================================
    //  CO4: Search foods using HashMap + linear text match
    // ============================================================
    public static void searchAndDisplay(String query, Scanner sc) {
        // CO4: Track recent searches in Deque (limited to 5)
        if (recentSearches.size() == 5) recentSearches.pollFirst();
        recentSearches.offerLast(query);

        List<FoodItem> results = linearSearchFood(query); // CO1: O(n)
        if (results.isEmpty()) {
            System.out.println("  No foods found matching: " + query);
            return;
        }
        // CO1: Sort results by name using bubble sort for demo
        List<FoodItem> sorted = bubbleSortByCalories(results);
        System.out.println("\n  Search results for \"" + query + "\" (" + sorted.size() + " found):");
        System.out.println("  " + "-".repeat(65));
        for (int i = 0; i < sorted.size(); i++) {
            System.out.printf("  %3d. [%-6s] %s%n", i + 1, sorted.get(i).category, sorted.get(i));
        }
        System.out.print("\n  Enter number to add to meal (0 to skip): ");
        // CO5: Exception handling for invalid input
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice >= 1 && choice <= sorted.size()) {
                System.out.print("  Enter quantity: ");
                double qty = Double.parseDouble(sc.nextLine().trim());
                FoodItem chosen = sorted.get(choice - 1);
                currentMeal.addItem(chosen, qty); // CO2: linked list insert
                undoStack.push(chosen.key);        // CO3: push to undo stack
                System.out.println("  Added: " + chosen.name + " x" + qty);
            }
        } catch (NumberFormatException e) {
            // CO5: Catch invalid number format
            System.out.println("  Invalid input: " + e.getMessage());
        }
    }

    // ============================================================
    //  CO2: View current meal (linked list traversal)
    // ============================================================
    public static void viewCurrentMeal() {
        if (currentMeal.isEmpty()) {
            System.out.println("  Meal is empty. Add some foods first!");
            return;
        }
        System.out.println("\n  CURRENT MEAL ITEMS (" + currentMeal.size() + "):");
        System.out.println("  " + "-".repeat(55));
        List<MealNode> items = currentMeal.getAll(); // CO2: O(n) traversal
        for (int i = 0; i < items.size(); i++) {
            MealNode n = items.get(i);
            System.out.printf("  %2d. %-25s  Qty: %.1f %s%n",
                    i + 1, n.food.name, n.quantity, n.food.serving);
        }
    }

    // ============================================================
    //  CO3: Undo last added item using Stack (LIFO)
    // ============================================================
    public static void undoLastAdd() {
        if (undoStack.isEmpty()) {
            System.out.println("  Nothing to undo.");
            return;
        }
        String lastKey = undoStack.pop(); // CO3: Stack pop O(1)
        boolean removed = currentMeal.removeItem(lastKey); // CO2: linked list delete
        if (removed) {
            System.out.println("  Undone: removed " + foodDatabase.get(lastKey).name);
        }
    }

    // ============================================================
    //  CO4: Browse by category using Map filtering
    // ============================================================
    public static void browseByCategory(String category, Scanner sc) {
        List<FoodItem> items = new ArrayList<>();
        // CO4: Iterate HashMap values - O(n)
        for (FoodItem f : foodDatabase.values()) {
            if (f.category.equals(category)) items.add(f);
        }
        // CO1: Selection sort by name for display - O(n²)
        for (int i = 0; i < items.size() - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < items.size(); j++) {
                if (items.get(j).name.compareTo(items.get(minIdx).name) < 0) minIdx = j;
            }
            FoodItem tmp = items.get(i);
            items.set(i, items.get(minIdx));
            items.set(minIdx, tmp);
        }
        System.out.println("\n  " + category.toUpperCase() + " foods (" + items.size() + "):");
        System.out.println("  " + "-".repeat(65));
        for (int i = 0; i < items.size(); i++) {
            System.out.printf("  %3d. %s%n", i + 1, items.get(i));
        }
        System.out.print("\n  Enter number to add to meal (0 to skip): ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice >= 1 && choice <= items.size()) {
                System.out.print("  Enter quantity: ");
                double qty = Double.parseDouble(sc.nextLine().trim());
                FoodItem chosen = items.get(choice - 1);
                currentMeal.addItem(chosen, qty); // CO2: linked list insert
                undoStack.push(chosen.key);        // CO3: stack push
                System.out.println("  Added: " + chosen.name);
            }
        } catch (NumberFormatException e) {
            // CO5: Exception handling
            System.out.println("  Invalid input: " + e.getMessage());
        }
    }

    // ============================================================
    //  CO4: Show recent searches stored in Deque
    // ============================================================
    public static void showRecentSearches() {
        if (recentSearches.isEmpty()) {
            System.out.println("  No recent searches.");
            return;
        }
        System.out.println("  Recent searches (Deque - CO4):");
        // CO4: Iterate Deque in order
        for (String s : recentSearches) System.out.println("    - " + s);
    }

    // ============================================================
    //  CO6: Main menu - full application entry point
    // ============================================================
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=".repeat(70));
        System.out.println("   MEAL BLENDER - Nutrition Calculator");
        System.out.println("   200 Foods: Vegetables | Fruits | Dairy | Indian");
        System.out.println("   DS Concepts: HashMap | LinkedList | Stack | Queue | Heap");
        System.out.println("=".repeat(70));

        boolean running = true;
        while (running) {
            // CO5: Main application loop
            System.out.println("\n  MAIN MENU");
            System.out.println("  1. Search food items              [CO1: Linear Search O(n)]");
            System.out.println("  2. Browse by category             [CO4: HashMap + Selection Sort]");
            System.out.println("  3. View current meal              [CO2: LinkedList traverse]");
            System.out.println("  4. Calculate nutrition            [CO5: Goal tracking]");
            System.out.println("  5. Undo last add                  [CO3: Stack LIFO]");
            System.out.println("  6. Top low-calorie foods          [CO3: Min-Heap]");
            System.out.println("  7. Top high-protein foods         [CO1: Merge Sort O(n log n)]");
            System.out.println("  8. Binary search food name        [CO1: Binary Search O(log n)]");
            System.out.println("  9. Recent searches                [CO4: Deque]");
            System.out.println("  10. Clear meal");
            System.out.println("  0. Exit");
            System.out.print("\n  Your choice: ");

            // CO5: Exception handling for menu input
            try {
                String input = sc.nextLine().trim();
                int choice  = Integer.parseInt(input);
                System.out.println();

                switch (choice) {
                    case 1:
                        System.out.print("  Search food name: ");
                        searchAndDisplay(sc.nextLine().trim(), sc);
                        break;
                    case 2:
                        System.out.println("  Categories: veg | fruit | dairy | indian");
                        System.out.print("  Enter category: ");
                        browseByCategory(sc.nextLine().trim().toLowerCase(), sc);
                        break;
                    case 3:
                        viewCurrentMeal();
                        break;
                    case 4:
                        calculateNutrition();
                        break;
                    case 5:
                        undoLastAdd();
                        break;
                    case 6:
                        System.out.print("  Category (all/veg/fruit/dairy/indian): ");
                        String cat6 = sc.nextLine().trim();
                        System.out.print("  How many to show? ");
                        int n6 = Integer.parseInt(sc.nextLine().trim());
                        showLowCalorieFoods(cat6, n6);
                        break;
                    case 7:
                        System.out.print("  Category (all/veg/fruit/dairy/indian): ");
                        String cat7 = sc.nextLine().trim();
                        System.out.print("  How many to show? ");
                        int n7 = Integer.parseInt(sc.nextLine().trim());
                        showHighProteinFoods(cat7, n7);
                        break;
                    case 8:
                        System.out.print("  Enter exact food name to binary search: ");
                        String bsQuery = sc.nextLine().trim();
                        int idx = binarySearchFood(bsQuery); // CO1: O(log n)
                        if (idx >= 0)
                            System.out.println("  Found at sorted index " + idx + ": " + sortedFoodNames.get(idx));
                        else
                            System.out.println("  Not found. Try linear search for partial matches.");
                        break;
                    case 9:
                        showRecentSearches();
                        break;
                    case 10:
                        currentMeal.clear(); // CO2: linked list clear
                        System.out.println("  Meal cleared.");
                        break;
                    case 0:
                        running = false;
                        System.out.println("  Thank you for using Meal Blender!");
                        break;
                    default:
                        System.out.println("  Invalid option. Please choose 0-10.");
                }
            } catch (NumberFormatException e) {
                // CO5: Catch and handle bad input
                System.out.println("  Please enter a valid number. (" + e.getMessage() + ")");
            } catch (Exception e) {
                // CO5: Catch any unexpected runtime errors
                System.out.println("  Error: " + e.getMessage());
            }
        }
        sc.close();
    }
}