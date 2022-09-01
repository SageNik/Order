
1. Order creation process:
    - Create order entity with initial status 'CREATED'
    - Chek in Account service whether user has enough money for pay and change status from 'CREATED' to
      'PAYMENT_IN_PROGRESS'
    - Payment service do a payment and change status from 'PAYMENT_IN_PROGRESS' to 'PAID'
    - Manufacture service run a producing and change status from 'PAID' to 'PRODUCTION_IN_PROGRESS'
      and when production is completed, change status from 'PRODUCTION_IN_PROGRESS' to 'READY_FOR_DELIVERY'
    - Delivery service run a delivery process and change status from 'READY_FOR_DELIVERY' to 'DELIVERY_IN_PROGRESS'
      and when delivery is completed, change status from 'DELIVERY_IN_PROGRESS' to 'COMPLETED'
    - If any exception is appeared then roll back all possible changes or do compensative transactions
      and change status from any to 'CANCELED'