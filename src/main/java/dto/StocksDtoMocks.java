package dto;

import com.example.tinkoffservice.model.Stock;
import com.example.tinkoffservice.model.StockMock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StocksDtoMocks {
    List<StockMock> stocks;
}
