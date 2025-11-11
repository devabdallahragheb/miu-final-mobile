const userService = require('../services/userService');
const { validationResult } = require('express-validator');
const { ValidationError } = require('../utils/errors');


class UserController {
  async createUser(req, res, next) {
    try {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        const errorMessages = errors.array().map(err => err.msg).join(', ');
        throw new ValidationError(errorMessages);
      }
      const user = await userService.createUser(req.body);
      res.status(201).json({
        success: true,
        message: 'User created successfully',
        data: user
      });
    } catch (error) {
      next(error); 
    }
  }

  async getAllUsers(req, res, next) {
    try {
      const page = parseInt(req.query.page) || 1;
      const limit = parseInt(req.query.limit) || 10;
      const filters = {
        name: req.query.name
      };
      const result = await userService.getAllUsers(page, limit, filters);

      res.status(200).json({
        success: true,
        message: 'Users retrieved successfully',
        data: result.users,
        pagination: result.pagination
      });
    } catch (error) {
      next(error);
    }
  }

  async getUserById(req, res, next) {
    try {
      const user = await userService.getUserById(req.params.id);
      res.status(200).json({
        success: true,
        message: 'User retrieved successfully',
        data: user
      });
    } catch (error) {
      next(error);
    }
  }

  async updateUser(req, res, next) {
    try {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        const errorMessages = errors.array().map(err => err.msg).join(', ');
        throw new ValidationError(errorMessages);
      }

      const user = await userService.updateUser(req.params.id, req.body);

      res.status(200).json({
        success: true,
        message: 'User updated successfully',
        data: user
      });
    } catch (error) {
      next(error);
    }
  }

  async deleteUser(req, res, next) {
    try {
      const user = await userService.deleteUser(req.params.id);

      res.status(200).json({
        success: true,
        message: 'User deleted successfully',
        data: user
      });
    } catch (error) {
      next(error);
    }
  }

}

module.exports = new UserController();
