package com.tourism.backend.controller;

import com.tourism.backend.entity.User;
import com.tourism.backend.enums.Role;
import com.tourism.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/v1/faker")
@RequiredArgsConstructor
public class UserFakerController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final List<FakeProvince> fakeProvinces = new ArrayList<>();

    @PostConstruct
    public void initLocationData() {
        FakeProvince hcm = new FakeProvince("79", "Thành phố Hồ Chí Minh");
        hcm.addDistrict("760", "Quận 1");
        hcm.addDistrict("761", "Quận 12");
        hcm.addDistrict("770", "Quận Gò Vấp");
        hcm.addDistrict("769", "Thành phố Thủ Đức");
        fakeProvinces.add(hcm);

        FakeProvince hn = new FakeProvince("01", "Thành phố Hà Nội");
        hn.addDistrict("001", "Quận Ba Đình");
        hn.addDistrict("002", "Quận Hoàn Kiếm");
        hn.addDistrict("005", "Quận Cầu Giấy");
        hn.addDistrict("006", "Quận Đống Đa");
        fakeProvinces.add(hn);

        FakeProvince dn = new FakeProvince("48", "Thành phố Đà Nẵng");
        dn.addDistrict("490", "Quận Liên Chiểu");
        dn.addDistrict("492", "Quận Hải Châu");
        dn.addDistrict("493", "Quận Sơn Trà");
        fakeProvinces.add(dn);

        FakeProvince ct = new FakeProvince("92", "Thành phố Cần Thơ");
        ct.addDistrict("916", "Quận Ninh Kiều");
        ct.addDistrict("917", "Quận Ô Môn");
        fakeProvinces.add(ct);
    }

    @PostMapping("/users")
    public ResponseEntity<?> generateFakeUsers(@RequestParam(defaultValue = "100") int count) {
        Faker faker = new Faker(new Locale("vi"));
        List<User> users = new ArrayList<>();
        Random random = new Random();
        String encodedPassword = passwordEncoder.encode("Password123");

        for (int i = 0; i < count; i++) {
            FakeProvince randomProvince = fakeProvinces.get(random.nextInt(fakeProvinces.size()));

            FakeDistrict randomDistrict = randomProvince.getDistricts().get(random.nextInt(randomProvince.getDistricts().size()));

            String fullName = faker.name().fullName();
            fullName = fullName.replaceAll("[^a-zA-ZÀ-ỹ\\s]", "").trim();

            String uniqueEmail = faker.internet().emailAddress().replace("@", "+" + UUID.randomUUID().toString().substring(0, 5) + "@");

            User user = User.builder()
                    .fullName(fullName)
                    .email(uniqueEmail)
                    .password(encodedPassword)
                    .provinceCode(randomProvince.getCode())
                    .provinceName(randomProvince.getName())
                    .districtCode(randomDistrict.getCode())
                    .districtName(randomDistrict.getName())
                    .role(Role.CUSTOMER)
                    .status(true)
                    .isEmailVerified(true)
                    .verificationToken(null)
                    .coinBalance(BigDecimal.valueOf(0))
                    .build();

            users.add(user);
        }

        userRepository.saveAll(users);

        return ResponseEntity.ok("Đã insert thành công " + users.size() + " user fake (Mật khẩu: Password123)");
    }

    @Data
    @AllArgsConstructor
    private static class FakeProvince {
        private String code;
        private String name;
        private List<FakeDistrict> districts;

        public FakeProvince(String code, String name) {
            this.code = code;
            this.name = name;
            this.districts = new ArrayList<>();
        }

        public void addDistrict(String code, String name) {
            this.districts.add(new FakeDistrict(code, name));
        }
    }

    @Data
    @AllArgsConstructor
    private static class FakeDistrict {
        private String code;
        private String name;
    }
}
