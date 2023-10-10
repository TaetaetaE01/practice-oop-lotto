package lotto.service;

import lotto.Domain.Lotto;
import lotto.Domain.WinningLotto;
import lotto.config.ErrorMessage;
import lotto.config.Prize;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LottoService {
    // MAGIC NUMBER
    private static final int PURCHASE_PRICE = 1000;
    private static final int SECOND_RANK_KEY = 7;
    private static final int LOTTO_LIST_SIZE = 6;
    private static final int LOTTO_NUMBER_RANGE_FIRST = 1;
    private static final int LOTTO_NUMBER_RANGE_LAST = 45;
    private static final int RANKING_LIST_SIZE = 8;
    private static final int SELECTED_FIVE = 5;
    private static final String SPLIT_UNIT = ",";

    // components
    private static WinningLotto winningLotto;

    public LottoService() {
        this.winningLotto = new WinningLotto();
    }

    // validation

    /**
     * [VALIDATION]
     * [INPUT] 1000원으로 나뉘는지 / 1000원보다 큰지
     */
    public void validatePurchaseAmount(int purchaseMoney) {
        if (purchaseMoney % PURCHASE_PRICE != 0) {
            throw new IllegalArgumentException(ErrorMessage.ERROR_PURCHASEMONEY_NO_DIVISION.getErrorMessage());
        }
        if (purchaseMoney < PURCHASE_PRICE) {
            throw new IllegalArgumentException(ErrorMessage.ERROR_PURCHASEMONEY_UNDER_1000.getErrorMessage());
        }
    }

    /**
     * [VALIDATION]
     * [INPUT] 정수 입력 / 1 ~ 45의 수 / winningNumList 중복확인
     */
    private void validateInputBonusNum(int bonusNum, List<Integer> winningNumList) {
        if (bonusNum < LOTTO_NUMBER_RANGE_FIRST && bonusNum > LOTTO_NUMBER_RANGE_LAST) {
            throw new IllegalArgumentException(ErrorMessage.ERROR_WINNINGNUMBER_UNREASONABLE_RANGE.getErrorMessage());
        }
        if (winningNumList.contains(bonusNum)) {
            throw new IllegalArgumentException(ErrorMessage.ERROR_BONUSNUM_DUPLICATE.getErrorMessage());
        }
    }

    // func

    /**
     * [FUNC]
     * 구입금액을 통한 개수 계산
     */
    public int calculatePurchaseAmount(int purchaseMoney) {
        validatePurchaseAmount(purchaseMoney);
        return purchaseMoney / PURCHASE_PRICE;
    }

    /**
     * [FUNC]
     * 로또 개수에 따른 로또번호 생성
     */
    public List<Lotto> createMember(int purchaseAmount) {
        List<Lotto> selectedLottoNum = new ArrayList<>();
        for (int i = 0; i < purchaseAmount; i++) {
            List<Integer> member = camp.nextstep.edu.missionutils.Randoms.pickUniqueNumbersInRange(LOTTO_NUMBER_RANGE_FIRST, LOTTO_NUMBER_RANGE_LAST, LOTTO_LIST_SIZE);
            Lotto lotto = new Lotto(member);
            selectedLottoNum.add(lotto);
        }
        return selectedLottoNum;
    }

    /**
     * [FUNC]
     * INPUT값으로 받은 당첨번호 리스트에 저장
     */
    public ArrayList<Integer> convertWinningNum(String winngingNumStr) {
        ArrayList<Integer> winningNumlist = new ArrayList<>();

        String[] tempStr = winngingNumStr.split(SPLIT_UNIT);
        for (String temp : tempStr) {
            winningNumlist.add(Integer.parseInt(temp));
        }
        winningLotto.setWinningNumlist(winningNumlist);
        return winningNumlist;
    }

    /**
     * [FUNC]
     * 당첨번호와 로또번호 비교 / (조건)BONUS번호도 비교
     */
    public int[] checkNum(List<Lotto> selectedLottoNumList, List<Integer> winningNumList, int bonusNum) {
        validateInputBonusNum(bonusNum, winningNumList);

        List<Integer> tempList;
        int[] checkedRankList = new int[RANKING_LIST_SIZE];

        for (Lotto selectedLott : selectedLottoNumList) {
            List<Integer> tempSelected = selectedLott.getLottoList();

            tempList = tempSelected.stream().filter(o -> winningNumList.stream()
                    .anyMatch(Predicate.isEqual(o))).collect(Collectors.toList());

            checkedRankList[tempList.size()]++;

            // 5개 맞췄을 때 bonus 검증
            // BONUS넘 가져와야함
            if (tempList.size() == SELECTED_FIVE && tempSelected.contains(bonusNum)) {
                checkedRankList[SECOND_RANK_KEY]++;
            }
        }
        return checkedRankList;
    }

    /**
     * [FUNC]
     * 당첨List에서 당첨금 합 / 수익률 계산
     */
    public double calEarningRate(int[] checkedRankList, int purchaseMoney) {
        double tempSum = 0.0;


        for (int i = 3; i < checkedRankList.length; i++) {
            if (checkedRankList[i] != 0 && i == 3) {
                tempSum += checkedRankList[i] * Prize.FIFTH.getWinningMoney();
            }
            if (checkedRankList[i] != 0 && i == 4) {
                tempSum += checkedRankList[i] * Prize.FOURTH.getWinningMoney();
            }
            if (checkedRankList[i] != 0 && i == 5) {
                tempSum += checkedRankList[i] * Prize.THIRD.getWinningMoney();
            }
            if (checkedRankList[i] != 0 && i == 6) {
                tempSum += checkedRankList[i] * Prize.FIRST.getWinningMoney();
            }
            if (checkedRankList[i] != 0 && i == 7) {
                tempSum += checkedRankList[i] * Prize.SECOND.getWinningMoney();
            }
        }
        double resultPercent = Math.round(tempSum / (double) purchaseMoney * 100.0) / 100.0 * 100;

        return resultPercent;
    }

}
