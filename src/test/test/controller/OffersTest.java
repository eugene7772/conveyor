package controller;

import static org.assertj.core.api.Assertions.assertThat;

import DTO.LoanOfferDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class OffersTest {

    @Autowired
    private Offers offers;
    @MockBean
    private LoanOfferDTO loanOfferDTO;

    @Test
    public void contextLoads() throws Exception {
        assertThat(offers).isNotNull();
    }
    @Test
    public static void testGetOffers(){
        List<LoanOfferDTO> offers = new ArrayList<>();
        assertThat(offers).isNotNull();
    }
}
