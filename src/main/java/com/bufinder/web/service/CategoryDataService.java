package com.bufinder.web.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryDataService {

    // Mirroring categories from original Android BU Finder app Dev_Type
    private final List<String> categories = List.of(
            "Audio Devices",
            "Laptop and Accessories",
            "Wearables",
            "Footwear",
            "Bottles",
            "Keys",
            "Sports Equipment",
            "Any type of Card",
            "Books and Stationery",
            "Personal Accessories",
            "Other"
    );

    // Mirroring subcategories from original Android Lost_Form / Found_Form / strings.xml
    private final Map<String, List<String>> subTypes = new LinkedHashMap<>();

    // Mirroring brand options from original Android Lost_Form / Found_Form / strings.xml
    private final Map<String, List<String>> brands = new LinkedHashMap<>();

    // Mirroring colour options from original Android arrayList_colour
    private final List<String> colors = List.of(
            "Black", "White", "Grey", "Silver", "Red", "Blue", "Light Blue", "Dark Blue",
            "Green", "Light Green", "Dark Green", "Yellow", "Orange", "Pink", "Violet",
            "Brown", "Gold", "Multi-color", "Other"
    );

    public CategoryDataService() {
        // Sub-types per category
        subTypes.put("Audio Devices", List.of("Speakers", "Over-ear Headphones", "On-ear Headphones", "Collar Earphones", "Wired Earphones", "True Wireless Earphones", "Other"));
        subTypes.put("Laptop and Accessories", List.of("Laptop", "Keyboard", "Mouse", "Storage Devices", "Charger", "Laptop Sleeve", "Power Bank", "Other"));
        subTypes.put("Wearables", List.of("Spectacles", "Watches", "Smartwatch / Fitness Band", "Jewellery", "Caps", "Mufflers", "Gloves", "Other"));
        subTypes.put("Footwear", List.of("Shoes", "Sneakers", "Slippers", "Sandals", "Other"));
        subTypes.put("Bottles", List.of("Steel Bottles", "Gym Bottles", "Insulated Flask", "Plastic Bottle", "Other"));
        subTypes.put("Keys", List.of("Car Keys", "Bike / Scooter Keys", "Room Keys", "Locker Keys", "Keyring / Lanyard", "Other"));
        subTypes.put("Sports Equipment", List.of("Racquets", "Ball", "Shuttle Cock", "Gym Gloves", "Cricket Bat", "Other"));
        subTypes.put("Any type of Card", List.of("College / Student ID Card", "Credit / Debit Card", "Driver's License", "Aadhaar / Government ID", "Metro / Transit Card", "Other"));
        subTypes.put("Books and Stationery", List.of("Notebook", "Textbook", "Pencil Box / Pouch", "Calculator", "Folder / Files", "Other"));
        subTypes.put("Personal Accessories", List.of("Wallet / Purse", "Backpack / Bag", "Umbrella", "Sunglasses", "Belt", "Other"));
        subTypes.put("Other", List.of("General Item", "Document", "Clothing", "Other"));

        // Brands per category
        brands.put("Audio Devices", List.of("Bose", "Apple", "Boat", "Noise", "Sony", "JBL", "Sennheiser", "Realme", "MI / Xiaomi", "OnePlus", "Other"));
        brands.put("Laptop and Accessories", List.of("Apple", "HP", "Dell", "Lenovo", "Asus", "Acer", "Logitech", "Samsung", "SanDisk", "Other"));
        brands.put("Wearables", List.of("Apple", "Samsung", "Titan", "Casio", "Titan Eye+", "Fastrack", "Fossil", "Oakley", "Ray-Ban", "Noise", "MI", "Boat", "G-Shock", "Other"));
        brands.put("Footwear", List.of("Nike", "Adidas", "Puma", "Reebok", "Sparx", "Campus", "Crocs", "Decathlon", "Bata", "Woodland", "Other"));
        brands.put("Bottles", List.of("Milton", "Cello", "Tupperware", "Hydro Flask", "Decathlon", "Borosil", "Other"));
        brands.put("Keys", List.of("Godrej", "Link", "Maruti Suzuki", "Hyundai", "Honda", "Tata", "Generic", "Other"));
        brands.put("Sports Equipment", List.of("Yonex", "Li-Ning", "Cosco", "Nivia", "Decathlon", "Adidas", "Nike", "Puma", "Wilson", "Other"));
        brands.put("Any type of Card", List.of("Bennett University / College ID", "HDFC Bank", "SBI", "ICICI Bank", "Axis Bank", "UCO Bank", "Slice", "Government of India", "Other"));
        brands.put("Books and Stationery", List.of("Classmate", "Navneet", "Casio (Calculator)", "Parker", "Camlin", "Doms", "Other"));
        brands.put("Personal Accessories", List.of("Wildcraft", "American Tourister", "Skybags", "Tommy Hilfiger", "Lavie", "Baggit", "Other"));
        brands.put("Other", List.of("Brand Not Applicable", "Can't Specify", "Other"));
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<String> getSubTypes(String category) {
        if (category == null || !subTypes.containsKey(category)) {
            return List.of("Other");
        }
        return subTypes.get(category);
    }

    public List<String> getBrands(String category) {
        if (category == null || !brands.containsKey(category)) {
            return List.of("Other");
        }
        return brands.get(category);
    }

    public List<String> getColors() {
        return colors;
    }

    public Map<String, List<String>> getAllSubTypesMap() {
        return Collections.unmodifiableMap(subTypes);
    }

    public Map<String, List<String>> getAllBrandsMap() {
        return Collections.unmodifiableMap(brands);
    }
}
