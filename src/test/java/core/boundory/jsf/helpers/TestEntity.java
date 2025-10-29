    package core.boundory.jsf.helpers;

import java.io.Serializable;

    public class TestEntity implements Serializable {
        private Long id;
        private String nombre;

        public TestEntity() {}

        public TestEntity(Long id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return "TestEntity{id=" + id + ", nombre='" + nombre + "'}";
        }
    }

