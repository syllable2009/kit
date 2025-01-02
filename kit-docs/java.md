# java中<? extends T>和<? super T>上界下界

1. 生产者用extends只能取不能存// 当你需要从集合中读取数据时
   public void readAnimals(List<? extends Animal> animals) {
   // 这里的集合是数据的生产者
   animals.forEach(Animal::makeSound);
   }

2. 消费者用super只能存不能取// 当你需要往集合中写入数据时
   public void addDogs(List<? super Dog> dogs) {
   // 这里的集合是数据的消费者
   dogs.add(new Dog());
   }
