import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val title: String?,
    val description: String?,
    val brand: String?,
    val category: String?,
    val thumbnail: String?,
    val price: Double?
)