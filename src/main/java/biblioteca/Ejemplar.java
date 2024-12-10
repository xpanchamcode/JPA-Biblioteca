package biblioteca;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "ejemplar")
public class Ejemplar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "isbn", nullable = false)
    private Libro isbn;

    @ColumnDefault("'Disponible'")
    @Lob
    @Column(name = "estado")
    private String estado;

    @OneToMany(mappedBy = "ejemplar")
    private Set<Prestamo> prestamos = new LinkedHashSet<>();

    public Set<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(Set<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Libro getIsbn() {
        return isbn;
    }

    public void setIsbn(Libro isbn) {
        this.isbn = isbn;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if(estado!=null && !estado.isEmpty()){
            if(estado.equalsIgnoreCase("Disponible") || estado.equalsIgnoreCase("Prestado") || estado.equalsIgnoreCase("Dañado")) {
                this.estado = estado;
            }
        }
        throw new IllegalArgumentException("Estado de ejemplar inválido.");
    }

    public Ejemplar() {
    }

    public Ejemplar(Libro isbn, String estado) {
        this.isbn = isbn;
        setEstado(estado);
    }

    @Override
    public String toString() {
        return "Ejemplar{" +
                "id=" + id +
                ", isbn=" + isbn.getIsbn() +
                ", estado='" + estado + '\'' +
                ", prestamos=" + prestamos +
                '}';
    }
}