const User = require('../models/User');
const { NotFoundError, ConflictError, 
 } = require('../utils/errors');


class UserService {

  async createUser(userData) {
    try {
      const existingUser = await User.findOne({ email: userData.email });
      if (existingUser) {
        throw new ConflictError('User with this email already exists');
      }
      const user = new User(userData);
      await user.save();
      return user;
    } catch (error) {
      throw error;
    }
  }

  async getAllUsers(page = 1, limit = 10, filters = {}) {
    try {
      const skip = (page - 1) * limit;
 
      const query = {};
      if (filters.name) query.name = new RegExp(filters.name, 'i');

      const users = await User.find(query)
        .skip(skip)
        .limit(limit)
        .sort({ createdAt: -1 });

      const total = await User.countDocuments(query);

      return {
        users,
        pagination: {
          currentPage: page,
          totalPages: Math.ceil(total / limit),
          totalUsers: total,
          hasMore: skip + users.length < total
        }
      };
    } catch (error) {
      throw error;
    }
  }

  async getUserById(userId) {
    try {
      const user = await User.findById(userId);
      
      if (!user) {
        throw new NotFoundError(`User with ID ${userId} not found`);
      }
      return user;
    } catch (error) {
      throw error;
    }
  }


  async updateUser(userId, updateData) {
    try {
      if (updateData.email) {
        const existingUser = await User.findOne({ 
          email: updateData.email,
          _id: { $ne: userId }
        });
        if (existingUser) {
          throw new ConflictError('Email already in use by another user ');
        }
      }
      const user = await User.findByIdAndUpdate(
        userId,
        { $set: updateData },
        { 
          new: true,
          runValidators: true
        }
      );

      if (!user) {
        throw new NotFoundError(`User with ID ${userId} not found`);
      }

      return user;
    } catch (error) {
      throw error;
    }
  }

  async deleteUser(userId) {
    try {
      const user = await User.findByIdAndDelete(userId);

      if (!user) {
        throw new NotFoundError(`User with ID ${userId} not found`);
      }

      return user;
    } catch (error) {
      throw error;
    }
  }
}

module.exports = new UserService();
