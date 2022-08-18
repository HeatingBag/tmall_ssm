/**
 * @Title: ProductAllComparator
 * @Auther: zhang
 * @Version: 1.0
 * @create: 2022/6/11 15:57
 */
package comparator;

import com.how2java.tmall.pojo.Product;

import java.util.Comparator;

public class ProductAllComparator implements Comparator<Product> {

    @Override
    public int compare(Product p1, Product p2) {
        return p2.getReviewCount() * p2.getSaleCount() - p1.getReviewCount() * p1.getSaleCount();
    }
}
