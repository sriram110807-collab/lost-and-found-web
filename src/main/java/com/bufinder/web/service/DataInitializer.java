package com.bufinder.web.service;

import com.bufinder.web.model.*;
import com.bufinder.web.repository.ClaimRepository;
import com.bufinder.web.repository.ItemRepository;
import com.bufinder.web.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ClaimRepository claimRepository;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${app.seed-data:true}")
    private boolean seedData;

    public DataInitializer(UserRepository userRepository, ItemRepository itemRepository,
                           ClaimRepository claimRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.claimRepository = claimRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!seedData) {
            log.info("app.seed-data is disabled. Skipping database initialization.");
            return;
        }

        if (userRepository.count() > 0) {
            log.info("Database already contains data ({} registered users). Skipping initial seeding to preserve existing users, reports, and claims.", userRepository.count());
            return;
        }

        log.info("Seeding initial administrator and demo users...");

        // 1. Admin user
        User admin = new User(
                "System Administrator",
                "admin@bufinder.com",
                passwordEncoder.encode("Admin@123"),
                "+91 9876543210",
                Role.ROLE_ADMIN
        );
        userRepository.save(admin);

        // 2. Demo students / users
        User demoUser = new User(
                "Rahul Sharma",
                "demo@bufinder.com",
                passwordEncoder.encode("User@123"),
                "+91 9811122334",
                Role.ROLE_USER
        );
        userRepository.save(demoUser);

        User alexUser = new User(
                "Ananya Patel",
                "ananya@bufinder.com",
                passwordEncoder.encode("User@123"),
                "+91 9922233445",
                Role.ROLE_USER
        );
        userRepository.save(alexUser);

        log.info("Seeding sample Lost and Found reports...");

        // Sample Item 1: Lost Dell XPS Laptop
        Item lostLaptop = new Item(
                "Silver Dell XPS 13 Laptop in Black Sleeve",
                ItemType.LOST,
                "Laptop and Accessories",
                "Laptop",
                "Dell",
                "Silver",
                "Central Library 2nd Floor Reading Hall",
                LocalDate.now().minusDays(2),
                "Left my Dell XPS 13 laptop in a black padded sleeve near study carrel #14 on Tuesday evening. Contains academic project work.",
                "Intel i7 sticker on bottom-left, small scratch near HDMI port, sticker of NASA on back lid",
                null,
                demoUser
        );
        itemRepository.save(lostLaptop);

        // Sample Item 2: Found Boat Headphones
        Item foundHeadphones = new Item(
                "Boat Rockerz 450 Bluetooth Headphones",
                ItemType.FOUND,
                "Audio Devices",
                "Over-ear Headphones",
                "Boat",
                "Black",
                "Auditorium Block - Seminar Room 204",
                LocalDate.now().minusDays(1),
                "Found black wireless over-ear headphones on the last row desk after the tech symposium.",
                "Left ear cushion has slight wear; blue indicator LED blink pattern",
                null,
                alexUser
        );
        itemRepository.save(foundHeadphones);

        // Sample Item 3: Lost Student ID Card
        Item lostIdCard = new Item(
                "Bennett University Student ID Card - CSE Dept",
                ItemType.LOST,
                "Any type of Card",
                "College / Student ID Card",
                "Bennett University / College ID",
                "Blue",
                "Main Dining Hall / Cafeteria",
                LocalDate.now().minusDays(3),
                "Lost my student RFID ID card with navy blue lanyard near the juice counter during lunchtime.",
                "Roll Number ends in 0241, card holder has barcode sticker on rear",
                null,
                demoUser
        );
        itemRepository.save(lostIdCard);

        // Sample Item 4: Found Titan Watch
        Item foundWatch = new Item(
                "Titan Men's Analog Quartz Wristwatch",
                ItemType.FOUND,
                "Wearables",
                "Watches",
                "Titan",
                "Brown",
                "Sports Complex Badminton Court #3",
                LocalDate.now().minusDays(1),
                "Found a brown leather strap wristwatch on the spectator bench near court 3.",
                "Engraving on stainless steel back case; Roman numerals on dial",
                null,
                admin
        );
        itemRepository.save(foundWatch);

        // Sample Item 5: Found Room Keys with Keyring
        Item foundKeys = new Item(
                "Set of 3 Room Keys with Silver Keyring",
                ItemType.FOUND,
                "Keys",
                "Room Keys",
                "Godrej",
                "Silver",
                "Hostel Block C - Ground Floor Reception",
                LocalDate.now().minusDays(4),
                "Found a set of 3 brass/silver keys attached to a red rubber Marvel keychain.",
                "Small yellow sticker with handwritten number on key #2",
                null,
                alexUser
        );
        foundKeys.setStatus(ItemStatus.CLAIM_PENDING);
        itemRepository.save(foundKeys);

        // Sample Claim on found keys
        Claim sampleClaim = new Claim(
                foundKeys,
                demoUser,
                demoUser.getFullName(),
                demoUser.getPhone(),
                demoUser.getEmail(),
                "These are my room keys! The Marvel keychain has Spider-Man on it, and key 2 has my locker number written in faint ink."
        );
        sampleClaim.setClaimDate(LocalDateTime.now().minusDays(1));
        claimRepository.save(sampleClaim);

        // Sample Item 6: Resolved Item
        Item resolvedItem = new Item(
                "Black Decathlon Gym Steel Water Bottle",
                ItemType.FOUND,
                "Bottles",
                "Steel Bottles",
                "Decathlon",
                "Black",
                "Gymnasium Weight Room",
                LocalDate.now().minusDays(5),
                "Found black 1-litre stainless steel bottle near the treadmill area.",
                "Dent on bottom rim, white gym club logo sticker",
                null,
                admin
        );
        resolvedItem.setStatus(ItemStatus.RESOLVED);
        itemRepository.save(resolvedItem);

        log.info("Database seeding completed successfully.");
    }
}
