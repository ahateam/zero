package zyxhj.core.repository;

import zyxhj.core.domain.Car;
import zyxhj.utils.data.ts.TSRepository;

public class CarRepository extends TSRepository<Car> {

	public CarRepository() {
		super(Car.class);
	}

}
