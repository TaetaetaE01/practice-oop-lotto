package lotto.repository;

import lotto.domain.Lotto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LottoRepository {
    private static final Map<Long, Lotto> store = new HashMap<>();
    private static long id = 0L;

    private static final LottoRepository instance = new LottoRepository();

    public LottoRepository getInstance() {
        return instance;
    }

    public LottoRepository() {

    }

    public Lotto save(Lotto lotto) {
        lotto.setId(++id);
        store.put(lotto.getId(), lotto);
        return lotto;
    }

    public List<Lotto> findAll() {
        return new ArrayList<>(store.values());
    }

    public int size() {
        return store.size();
    }
}
