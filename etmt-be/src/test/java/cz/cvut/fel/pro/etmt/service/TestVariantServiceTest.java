package cz.cvut.fel.pro.etmt.service;

import com.github.jesg.dither.Dither;
import cz.cvut.fel.pro.etmt.util.Utils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestVariantServiceTest {

    public void ditherExample_aetg() {
        // the minimum questions for test variants generation is 4
        // each equivalence class has to have at least 2 options
        // also, at least 2 equivalence classes need to be defined
        int t = 2; // t = 2 means pairwise strategy, t=n for n-wise
        Object[][] questions = new Object[][]{
                new Object[]{"3b_q1", "3b_q2"},
                new Object[]{"3b_q4", "3b_q5", "3b_q6"},
                new Object[]{"5b_q1", "5b_q2"},
                new Object[]{"8b_q1", "8b_q2"}
        };
        Object[][] variants = Dither.aetg(t, questions);

        Utils.log2DArray(variants);
    }

}
