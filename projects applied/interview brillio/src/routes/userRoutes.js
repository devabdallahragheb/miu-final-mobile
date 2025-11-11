const express = require('express');
const router = express.Router();
const userController = require('../controllers/userController');
const { userValidators } = require('../middleware/validators');
router.get(
  '/',
  userValidators.listQuery,
  userController.getAllUsers.bind(userController)
);
router.get(
  '/:id',
  userValidators.idParam,
  userController.getUserById.bind(userController)
);
router.post(
  '/',
  userValidators.create,
  userController.createUser.bind(userController)
);
router.put(
  '/:id',
  [...userValidators.idParam, ...userValidators.update],
  userController.updateUser.bind(userController)
);
router.delete(
  '/:id',
  userValidators.idParam,
  userController.deleteUser.bind(userController)
);
module.exports = router;
