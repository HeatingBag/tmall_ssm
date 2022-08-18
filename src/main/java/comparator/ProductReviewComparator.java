/**
 * @Title: ProductReviewComparator
 * @Auther: zhang
 * @Version: 1.0
 * @create: 2022/6/11 16:02
 */
package comparator;

import com.how2java.tmall.pojo.Product;

import java.util.Comparator;

public class ProductReviewComparator implements Comparator<Product> {
    @Override
    public int compare(Product p1, Product p2) {
        return p2.getReviewCount() - p1.getReviewCount();
    }
}
